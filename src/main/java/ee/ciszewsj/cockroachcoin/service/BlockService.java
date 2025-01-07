package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BlockService {

	@Getter
	private List<BlockDto> blockList;
	private final List<MinerService> observers = new ArrayList<>();

	private final CommunicationService communicationService;
	private final AccountService accountService;
	private final CertificatesFileStoreProperties properties;

	@Lazy
	@Autowired
	TransactionService transactionService;
	private int newBlockFails = 0;

	public BlockService(List<BlockDto> blockList, CommunicationService communicationService, AccountService accountService, CertificatesFileStoreProperties properties) {
		this.blockList = blockList;
		this.communicationService = communicationService;
		this.accountService = accountService;
		this.properties = properties;
		accountService.recalculate(blockList);
	}

	public static boolean validateBlockChain(List<BlockDto> blockList) {
		for (int i = 0; i < blockList.size() - 1; i++) {
			if (!blockList.get(i).validateHash(blockList.get(i + 1).previousHash(), blockList.get(i + 1).previousNonce())) {
				log.warn("blockchainValidationINCORRECT HASH FOR {}", i);
				return false;
			}
		}
		return true;
	}

	public boolean validateWithNewElement(BlockDto newElem) {
		List<BlockDto> newList = new ArrayList<>(blockList);
		newList.add(newElem);
		for (int i = 0; i < newList.size() - 1; i++) {
			if (!newList.get(i).validateHash(newList.get(i + 1).previousHash(), newList.get(i + 1).previousNonce())) {
				log.warn("newElementValidationINCORRECT HASH FOR {}", i);
				return false;
			}
		}
		return true;
	}

	public void onNewBlockReceived(BlockDto blockDto) throws InterruptedException {
		if (properties.impostor()) {
			return;
		}
		log.info("received a new block posted");
		if (!(blockDto.transactions().size() == 1
				&& blockDto.transactions().getFirst().type() == Transaction.TYPE.GENESIS
				&& blockDto.transactions().getFirst().senders().isEmpty()
				&& blockDto.transactions().getFirst().receivers().size() == 1
		)) {
			throw new IllegalStateException("NOT VALID BLOCK");
		}
		var newBlockList = new ArrayList<>(blockList);
		// if it's the current first block, just skip it
		if (blockDto.equals(blockList.getLast())) {
			log.info("Block already contained, skipping...");
			return;
		}
		if (properties.impostor()) {
			log.warn("Make impostor block");
			return;
		}
		newBlockList.add(blockDto);
		if (validateBlockChain(newBlockList)) {
			blockList = newBlockList;
			accountService.doTransaction(blockDto.transactions().getFirst());
			blockList = newBlockList;
			log.info("Add new block from another source [block={}]", blockDto);
			communicationService.onNewBlock(blockDto);
			notifyObservers();
			newBlockFails = 0;
		} else {
			// Blockchain validation fails, there's an incorrect hash for some x
			// What if we counted these fails, and if there are more than a certain number, then we fetch for a new blockchain (and if it's longer than ours, we replace ours)
			log.error("Wrong block [block={}]", blockDto);
			newBlockFails++;
			if (newBlockFails > communicationService.nodeService.getNodes().size() - 1) {
				log.info("Number of blocks validation failed is high ({}), asking for blockchain", newBlockFails);
				Thread.sleep(1000);
				// now fetch to a node for a new blockchain and compare it
				List<BlockDto> blockchainToCompare = communicationService.askForANewBlockchain();
				onNewBlockChainReceived(blockchainToCompare);
				newBlockFails = 0;
			}
		}
		accountService.recalculate(blockList);
	}

	public void onNewBlockChainReceived(List<BlockDto> blockChain) {
		if (properties.impostor()) {
			return;
		}
		log.info("received a new blockchain posted");
		if (validateBlockChain(blockChain)) {
			if (blockChain.size() <= blockList.size()) {
				log.error("SMALLER SIZE OF BLOCKCHAIN than what we have!");
				return;
			}
		} else {
			log.error("BLOCKCHAIN NOT CORRECT!!!");
//			throw new IllegalStateException("INCORRECT HASH");
			return;

		}
		if (!blockList.getFirst().equals(blockChain.getFirst())) {
			log.error("THIS IS ANOTHER whole CRYPTO!!!");
//			throw new IllegalStateException("THIS IS ANOTHER whole CRYPTO!!!");
			return;
		}
		List<Transaction> notCommittedTransaction =
				blockList.stream()
						.flatMap(block -> block.transactions().stream())
						.filter(transaction -> blockChain.stream()
								.flatMap(blockDto -> blockDto.transactions().stream())
								.filter(t -> t.type() == Transaction.TYPE.TRANSFER)
								.noneMatch(t -> t == transaction))
						.toList();

		blockList.clear();
		blockList.addAll(blockChain);
		log.debug("CHANGED BLOCKCHAIN SUCCESSFULLY [blockChain={}]", blockChain);
		notifyObservers();

		for (Transaction transaction : notCommittedTransaction) {
			if (blockChain.getLast().transactions().stream().anyMatch(t -> t.timestamp() == transaction.timestamp())) {
				log.info("Could not be 2 with same timestamp");
				return;
			}
			try {
				transactionService.recalculateTransaction(transaction);
			} catch (Exception e) {
				log.error("Could not add uncommitted transaction", e);
			}
		}
		accountService.recalculate(blockList);
	}

	public synchronized void addNew(BlockDto dto) {
		if (validateWithNewElement(dto)) {
			log.info("new element validation is correct, propagating the block...");
			blockList.add(dto);
			communicationService.onNewBlock(dto);
		}

		notifyObservers();
		accountService.recalculate(blockList);
	}

	public BlockDto getLast() {
		return blockList.getLast();
	}

	public void addObserver(MinerService observer) {
		observers.add(observer);
	}


	private void notifyObservers() {
		for (MinerService observer : observers) {
			observer.listUpdated();
		}
	}
}
