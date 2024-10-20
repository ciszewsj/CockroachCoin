package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.NOT_FOUND_EXCEPTION;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRepository {
	private final List<Account> accountList = List.of(
			new Account("acc1", 1000L),
			new Account("acc2", 1000L),
			new Account("acc3", 1000L)
	);

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
