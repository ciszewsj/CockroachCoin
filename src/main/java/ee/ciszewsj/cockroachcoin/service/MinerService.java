package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.ToTransactionField;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MinerService {
	private final Clock clock;
	private final BlockService blockService;
	private final AccountService accountService;
	private final CertificatesFileStoreProperties properties;
	private Thread miningThread;
	private AtomicBoolean isMining = new AtomicBoolean(true);

	public MinerService(Clock clock, AccountService accountService, BlockService blockService, CertificatesFileStoreProperties certificatesFileStoreProperties) {
		this.clock = clock;
		this.accountService = accountService;
		this.blockService = blockService;
		this.properties = certificatesFileStoreProperties;
		this.blockService.addObserver(this);
		startMiner();
	}

	public void mineBlock(BlockDto previousBlock, AtomicBoolean isMining) {
		long nonce = 0;
		log.info("Start mining [minerKey={}]", properties.minerKey());
		while (isMining.get()) {
			String hash = previousBlock.calculateHash(nonce);
			if (hash.startsWith("0".repeat(properties.difficulty()))) {
				Transaction reward = minedTransaction();
				BlockDto newBlock = new BlockDto(previousBlock.index() + 1,
						new ArrayList<>(List.of(reward)),
						clock.millis(),
						nonce,
						hash
				);
				blockService.addNew(newBlock);
				accountService.doTransaction(reward);
				log.info("Block mined [index={}, hash={}, by={}]", newBlock.index(), hash, properties.minerKey());
				break;
			}
			nonce++;
		}
		log.info("Stop mining without success [minerKey={}]", properties.minerKey());
	}

	private Transaction minedTransaction() {
		return new Transaction(0, List.of(), List.of(new ToTransactionField(properties.minerKey(), properties.award())), "", clock.millis(), Transaction.TYPE.GENESIS);
	}

	public void startMiner() {
		if (!properties.minerKey().isEmpty()) {
			miningThread = new Thread(() -> mineBlock(blockService.getLast(), isMining));
			miningThread.start();
		}

	}

	public void listUpdated() {
		if (miningThread != null) {
			isMining.set(false);
			isMining = new AtomicBoolean(true);
			miningThread.interrupt();
			miningThread = new Thread(() -> mineBlock(blockService.getLast(), isMining));
			miningThread.start();
		}
	}
}
