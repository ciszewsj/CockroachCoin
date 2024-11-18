package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;

@Slf4j
public class MinerService {
	private final Clock clock;
	private final BlockService blockService;

	private Thread miningThread;
	int difficulty = 6;

	public MinerService(Clock clock, BlockService blockService) {
		this.clock = clock;
		this.blockService = blockService;
		this.blockService.addObserver(this);
		miningThread = new Thread(() -> mineBlock(blockService.getLast()));
		miningThread.start();
	}

	public void mineBlock(BlockDto previousBlock) {
		long nonce = 0;
		while (true) {
			String hash = previousBlock.calculateHash(nonce);
			if (hash.startsWith("0".repeat(difficulty))) {

				BlockDto newBlock = new BlockDto(previousBlock.index() + 1,
						new ArrayList<>(),
						clock.millis(),
						nonce,
						hash
				);
				blockService.addNew(newBlock);
				log.error("BLOCK MINED {} >>> {}", newBlock.index(), hash);
				break;
			}
			nonce++;
		}
	}

	public void listUpdated() {
		miningThread.interrupt();
		miningThread = new Thread(() -> mineBlock(blockService.getLast()));
		miningThread.start();
	}
}
