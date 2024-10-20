package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final AccountRepository accountRepository;

	public void doTransaction(String sender, String receiver, long amount, String secret) {
		Account senderAccount = accountRepository.findAccountWithAuthentication(sender, secret);
		Account reciverAccount = accountRepository.findAccount(receiver);
		if (senderAccount.getBalance() >= amount) {
			senderAccount.setBalance(senderAccount.getBalance() - amount);
			reciverAccount.setBalance(reciverAccount.getBalance() + amount);
			log.info("Do transaction [sender={}, receiver={}, amount={}]", secret, receiver, amount);
		} else {
			log.info("Could not do operation over limit [sender={}, receiver={}, amount={}]", sender, receiver, amount);
			throw new IllegalStateException();
		}
	}
}
