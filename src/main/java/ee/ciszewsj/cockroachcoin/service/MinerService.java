package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.CockroachcoinApplication;
import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.ToTransactionField;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class MinerService {

	private final Clock clock;
	private final BlockService blockService;
	private final AccountService accountService;
	private final CertificatesFileStoreProperties properties;
	private Thread miningThread;
	public AtomicBoolean isMining = new AtomicBoolean(true);

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
			if (hash.startsWith("0".repeat(CockroachcoinApplication.DIFFICULTY))) {
				Transaction reward = minedTransaction();
				BlockDto newBlock = new BlockDto(previousBlock.index() + 1,
						new ArrayList<>(List.of(reward)),
						clock.millis(),
						nonce,
						hash
				);
				log.info("BLOCK MINED! [index={}, hash={}, by={}]", newBlock.index(), hash, properties.minerKey());
				blockService.addNew(newBlock);
				accountService.doTransaction(reward);
				break;
			}
			nonce++;
		}
		log.info("Stopped mining for this thread. [minerKey={}]", properties.minerKey());
	}

	private Transaction minedTransaction() {
		return new Transaction(0, List.of(), List.of(new ToTransactionField(properties.minerKey(), CockroachcoinApplication.REWARD)), "", clock.millis(), Transaction.TYPE.GENESIS);
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
