package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class BlockService {

	int difficulty = 1;

	private final List<BlockDto> blockList;


	public void mine() {
		log.error("??? {}", blockList.size());
		BlockDto lastBlock = blockList.getLast();
		long retries = 0;
		long maxRange = (long) Math.pow(2, 24);
		long nonce;
		String lastBlockHexHash = Integer.toHexString(lastBlock.hashCode());
		String targetPrefix = "0".repeat(difficulty);


		while (!lastBlockHexHash.startsWith(targetPrefix)) {
			nonce = new Random().nextLong();
			lastBlockHexHash = Integer.toHexString(lastBlock.hashCode());
			retries++;

			if (2 * retries > maxRange) {
				maxRange *= 2;
				log.error("RETRIES:{}, maxRange:{}, nonce:{}", retries, maxRange, nonce);
			}
		}
		log.error("BLOCK MINED");
	}


	public void findNewChains() {

	}
}
