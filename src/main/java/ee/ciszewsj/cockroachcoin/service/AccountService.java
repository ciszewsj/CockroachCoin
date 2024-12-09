package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
	private Map<String, Long> balances = new HashMap<>();

	public long getBalance(String publicKey) {
		return balances.getOrDefault(publicKey, 0L);
	}

	public void recalculate(List<BlockDto> blockChain) {
		Map<String, Long> temporaryMap = new HashMap<>(balances);
		for (var block : blockChain) {
			for (var transaction : block.transactions()) {
				for (var sender : transaction.senders()) {
					var balance = temporaryMap.getOrDefault(sender.publicKey(), 0L);
					balance -= sender.amount();
					if (balance < 0) {
						throw new IllegalStateException("AMOUNT ON ACCOUNT < 0");
					}
					temporaryMap.put(sender.publicKey(), balance);
				}
				for (var receiver : transaction.receivers()) {
					var balance = temporaryMap.getOrDefault(receiver.publicKey(), 0L);
					balance += receiver.amount();
					temporaryMap.put(receiver.publicKey(), balance);
					balances = temporaryMap;
				}
			}
		}
		balances = temporaryMap;
	}

	public synchronized void doTransaction(Transaction request) {
		Map<String, Long> temporaryMap = new HashMap<>(balances);

		for (var sender : request.senders()) {
			long balance = temporaryMap.getOrDefault(sender.publicKey(), 0L);
			balance -= sender.amount();

			if (balance < 0) {
				throw new IllegalStateException("AMOUNT ON ACCOUNT < 0");
			}

			temporaryMap.put(sender.publicKey(), balance);
			log.info("Update balance for sender [balance={}, publicKey={}]", balance, sender.publicKey());
		}

		for (var receiver : request.receivers()) {
			long balance = temporaryMap.getOrDefault(receiver.publicKey(), 0L);
			balance += receiver.amount();

			temporaryMap.put(receiver.publicKey(), balance);
			log.info("Update balance for receiver [balance={}, publicKey={}]", balance, receiver.publicKey());
		}

		balances.clear();
		balances.putAll(temporaryMap);
	}

}
