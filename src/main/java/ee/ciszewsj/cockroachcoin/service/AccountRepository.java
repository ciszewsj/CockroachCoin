package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.INTERNAL_SERVER_EXCEPTION;
import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.NOT_FOUND_EXCEPTION;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRepository {
	private final CertificatesService certificatesService;

	private final List<Account> accountList = new ArrayList<>();

	public Account findAccount(String accountName) {
		return accountList.stream()
				.filter(account -> account.getName().equals(accountName))
				.findAny()
				.orElseThrow(() -> {
					log.warn("Could not find account [accountName={}]", accountName);
					return NOT_FOUND_EXCEPTION;
				});
	}

	public Account findAccountOrCreate(String accountName, String signature, String publicKey) {
		Optional<Account> accountOptional = accountList.stream()
				.filter(account -> account.getName().equals(accountName))
				.findAny();
		if (accountOptional.isPresent()) {
			certificatesService.verifyObjectWithSignature(accountName, accountName, signature);
			return accountOptional.get();
		} else {
			if (publicKey == null) {
				throw NOT_FOUND_EXCEPTION;
			}
			try {
				certificatesService.savePublicKey(accountName, publicKey);
				Account newAccount = new Account(accountName, 0L);
				accountList.add(newAccount);
				log.info("Create new account [account={}]", newAccount);
				return newAccount;
			} catch (Exception e) {
				log.error("Error during saving publicKey", e);
				throw INTERNAL_SERVER_EXCEPTION;
			}
		}
	}
}
