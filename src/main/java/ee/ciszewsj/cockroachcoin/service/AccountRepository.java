package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.NOT_FOUND_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRepository {
	private final List<Account> accountList = List.of(
			new Account("Acc1", "Acc1", 1000L),
			new Account("Acc2", "Acc2", 1000L),
			new Account("Acc3", "Acc3", 1000L)
	);

	public Account findAccountWithAuthentication(String accountName, String secret) {
		return accountList.stream()
				.filter(account -> account.getName().equals(accountName) && account.getSecret().equals(secret))
				.findAny()
				.orElseThrow(() -> {
					log.warn("Could not find account with authentication [accountName={}, secret={}]", accountName, secret);
					return NOT_FOUND_EXCEPTION;
				});
	}

	public Account findAccount(String accountName) {
		return accountList.stream()
				.filter(account -> account.getName().equals(accountName))
				.findAny()
				.orElseThrow(() -> {
					log.warn("Could not find account [accountName={}]", accountName);
					return NOT_FOUND_EXCEPTION;
				});
	}
}
