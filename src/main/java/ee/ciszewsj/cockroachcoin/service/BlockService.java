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

	private final CommunicationService communicationService;

	public boolean validateBlockChain() {
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

	public void postNewBlockChain(List<BlockDto> blockChain) {
		if (validateBlockChain()) {
			if (blockChain.size() <= blockList.size()) {
				log.error("SMALLER SIZE OF BLOCKCHAIN!");
//				throw new IllegalStateException("SMALLER SIZE OF BLOCKCHAIN");
				return;
			}
		}
		for (int i = 0; i < blockChain.size() - 1; i++) {
			if (!blockChain.get(i).validateHash(blockChain.get(i + 1).previousHash(), blockChain.get(i + 1).previousNonce())) {
				log.error("NOT CORRECT!!!");
				throw new IllegalStateException("INCORRECT HASH");
			}
		}
		blockList.clear();
		blockList.addAll(blockChain);
		notifyObservers();
	}

	public synchronized void addNew(BlockDto dto) {
		if (validateWithNewElement(dto)) {
			blockList.add(dto);
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
		communicationService.onBlockChange(blockList);
	}
}
