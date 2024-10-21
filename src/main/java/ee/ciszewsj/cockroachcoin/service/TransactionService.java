package ee.ciszewsj.cockroachcoin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import io.swagger.v3.core.util.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.INTERNAL_SERVER_EXCEPTION;
import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.TRANSACTION_OVER_LIMIT_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final AccountRepository accountRepository;

	private final Clock clock;

	@Getter
	private final List<Transaction> transactionList = new ArrayList<>();

	public void doDeposit(String receiver, long amount) {
		try {
			Account senderAccount = accountRepository.findAccount(receiver);
			senderAccount.addAmount(amount);
			transactionList.add(new Transaction(amount, "", receiver, null, calculatePreviousHash(), clock.millis(), Transaction.TYPE.DEPOSIT));
		} catch (Exception e) {
			log.error("Could not make deposit");
		}
	}

	public void doTransaction(String sender, String receiver, long amount, String signature) {
		Account senderAccount = accountRepository.findAccount(sender);
		Account reciverAccount = accountRepository.findAccount(receiver);
		if (senderAccount.getBalance() >= amount) {
			String hash;
			try {
				hash = calculatePreviousHash();
			} catch (JsonProcessingException | NoSuchAlgorithmException e) {
				log.error("Error during calculating hash");
				throw INTERNAL_SERVER_EXCEPTION;
			}
			transactionList.add(new Transaction(amount, sender, receiver, signature, hash, clock.millis(), Transaction.TYPE.TRANSFER));
			senderAccount.subAmount(amount);
			reciverAccount.addAmount(amount);
			log.info("Do transaction [sender={}, receiver={}, amount={}]", sender, receiver, amount);
		} else {
			log.info("Could not do operation over limit [sender={}, receiver={}, amount={}]", sender, receiver, amount);
			throw TRANSACTION_OVER_LIMIT_EXCEPTION;
		}
	}

	public void addTransaction(List<Transaction> list) {
		transactionList.addAll(list);
	}


	private String calculatePreviousHash() throws JsonProcessingException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return calculateHash(Json.mapper().writeValueAsString(transactionList.getLast()));
	}


	private static String calculateHash(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(input.getBytes());

		StringBuilder hexString = new StringBuilder();
		for (byte b : hashBytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();

	}
}
