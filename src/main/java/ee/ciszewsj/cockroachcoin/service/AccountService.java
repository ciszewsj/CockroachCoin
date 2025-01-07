package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
	private Map<String, Long> balances = new HashMap<>();
	private final Lock lock = new ReentrantLock();

	public long getBalance(String publicKey) {
		return balances.getOrDefault(publicKey, 0L);
	}

	public void recalculate(List<BlockDto> blockChain) {
		lock.lock();
		Map<String, Long> temporaryMap = new HashMap<>();
		for (var block : blockChain) {
			for (var transaction : block.transactions()) {
				for (var sender : transaction.senders()) {
					var balance = temporaryMap.getOrDefault(sender.senderKey(), 0L);
					balance -= sender.amount();
					if (balance < 0) {
						lock.unlock();
						throw new IllegalStateException("AMOUNT ON ACCOUNT < 0");
					}
					temporaryMap.put(sender.senderKey(), balance);
				}
				for (var receiver : transaction.receivers()) {
					var balance = temporaryMap.getOrDefault(receiver.senderKey(), 0L);
					balance += receiver.amount();
					temporaryMap.put(receiver.senderKey(), balance);
					balances = temporaryMap;
				}
			}
		}
		balances = temporaryMap;
		lock.unlock();
	}

	public synchronized void doTransaction(Transaction request) {
		lock.lock();
		Map<String, Long> temporaryMap = new HashMap<>(balances);
		for (var sender : request.senders()) {
			long balance = temporaryMap.getOrDefault(sender.senderKey(), 0L);
			balance -= sender.amount();

			if (balance < 0) {
				lock.unlock();
				throw new IllegalStateException("AMOUNT ON ACCOUNT < 0");
			}

			temporaryMap.put(sender.senderKey(), balance);
			log.info("Update balance for sender [balance={}, senderKey={}]", balance, sender.senderKey());
		}

		for (var receiver : request.receivers()) {
			long balance = temporaryMap.getOrDefault(receiver.senderKey(), 0L);
			balance += receiver.amount();

			temporaryMap.put(receiver.senderKey(), balance);
			log.info("Update balance for receiver [balance={}, senderKey={}]", balance, receiver.senderKey());
		}

		balances.clear();
		balances.putAll(temporaryMap);
		lock.unlock();
	}
}
