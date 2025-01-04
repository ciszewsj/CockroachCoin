package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	public BlockService(List<BlockDto> blockList, CommunicationService communicationService, AccountService accountService) {
		this.blockList = blockList;
		this.communicationService = communicationService;
		this.accountService = accountService;
	}

	public static boolean validateBlockChain(List<BlockDto> blockList) {
		for (int i = 0; i < blockList.size() - 1; i++) {
			if (!blockList.get(i).validateHash(blockList.get(i + 1).previousHash(), blockList.get(i + 1).previousNonce())) {
				log.warn("INCORRECT HASH FOR {}", i);
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
				log.warn("INCORRECT HASH FOR {}", i);
				return false;
			}
		}
		return true;
	}

	public void postNewBlock(BlockDto blockDto) {
		if (!(blockDto.transactions().size() == 1
				&& blockDto.transactions().getFirst().type() == Transaction.TYPE.GENESIS
				&& blockDto.transactions().getFirst().senders().isEmpty()
				&& blockDto.transactions().getFirst().receivers().size() == 1
		)) {
			throw new IllegalStateException("NOT VALID BLOCK");
		}
		var newBlockList = new ArrayList<>(blockList);
		newBlockList.add(blockDto);
		if (validateBlockChain(newBlockList)) {
			blockList = newBlockList;
			accountService.doTransaction(blockDto.transactions().getFirst());
			blockList = newBlockList;
			log.error("Add new block from another source [block={}]", blockDto);
			communicationService.onNewBlock(blockDto);
			notifyObservers();
		} else {
			log.error("Wrong block [block={}]", blockDto);
		}
	}

	public void postNewBlockChain(List<BlockDto> blockChain) {
		if (validateBlockChain(blockList)) {
			if (blockChain.size() <= blockList.size()) {
				log.error("SMALLER SIZE OF BLOCKCHAIN!");
				return;
			}
		} else {
			log.error("BLOCKCHAIN NOT CORRECT!!!");
			throw new IllegalStateException("INCORRECT HASH");

		}
		if (blockList.getFirst() != blockChain.getFirst()) {
			throw new IllegalStateException("THIS IS ANOTHER CRYPTO!!!");
		}
		blockList.clear();
		blockList.addAll(blockChain);
		log.debug("Change blockchain successfully [blockChain={}]", blockChain);
		accountService.recalculate(blockChain);
		notifyObservers();
	}

	public synchronized void addNew(BlockDto dto) {
		if (validateWithNewElement(dto)) {
			blockList.add(dto);
			communicationService.onNewBlock(dto);
		}

		notifyObservers();
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
