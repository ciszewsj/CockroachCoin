package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.TRANSACTION_OVER_LIMIT_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final AccountRepository accountRepository;

	@Getter
	private final List<Transaction> transactionList = new ArrayList<>();

	public void doTransaction(String sender, String receiver, long amount, String secret) {
		Account senderAccount = accountRepository.findAccount(sender);
		Account reciverAccount = accountRepository.findAccount(receiver);
		if (senderAccount.getBalance() >= amount) {
			transactionList.add(new Transaction(sender, receiver, amount));
			senderAccount.setBalance(senderAccount.getBalance() - amount);
			reciverAccount.setBalance(reciverAccount.getBalance() + amount);
			log.info("Do transaction [sender={}, receiver={}, amount={}]", secret, receiver, amount);
		} else {
			log.info("Could not do operation over limit [sender={}, receiver={}, amount={}]", sender, receiver, amount);
			throw TRANSACTION_OVER_LIMIT_EXCEPTION;
		}
	}
}
