package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BlockService {

	@Getter
	private final List<BlockDto> blockList;
	private final List<MinerService> observers = new ArrayList<>();


	public void postNewBlockChain(List<BlockDto> blockChain) {
		if (blockChain.size() <= blockList.size()) {
			log.error("SMALLER SIZE OF BLOCKCHAIN!");
			throw new IllegalStateException("SMALLER SIZE OF BLOCKCHAIN");
		}
		for (int i = 0; i < blockChain.size() - 1; i++) {
			if (blockChain.get(i).validateHash(blockChain.get(i + 1).previousHash(), blockChain.get(i + 1).previousNonce())) {
				log.error("NOT CORRECT!!!");
				throw new IllegalStateException("INCORRECT HASH");
			}
		}
		blockList.clear();
		blockList.addAll(blockChain);
		notifyObservers();
	}

	public void addNew(BlockDto dto) {
		blockList.add(dto);
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
