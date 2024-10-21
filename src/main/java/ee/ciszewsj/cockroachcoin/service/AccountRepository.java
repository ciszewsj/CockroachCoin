package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.INTERNAL_SERVER_EXCEPTION;
import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.NOT_FOUND_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRepository {
	private final CertificatesService certificatesService;
	private final SyncService syncService;

	private final Map<String, Long> accounts = new HashMap<>();

	public List<AccountDto> getAccounts() {
		return accounts.keySet().stream().map(

				account -> {
					try {
						return new AccountDto(account, certificatesService.readPublicKeyString(account));
					} catch (IOException e) {
						log.error("READING PK NOT WORKING", e);
						return new AccountDto(account, null);
					}
				}
		).toList();
	}

	public Account findAccount(String accountName) {
		if (accounts.containsKey(accountName)) {
			return new Account(accountName, accounts.get(accountName));
		}
		log.warn("Could not find account [accountName={}]", accountName);
		throw NOT_FOUND_EXCEPTION;
	}

	synchronized public Account findAccountOrCreate(String accountName, String signature, String publicKey) {
		if (accounts.containsKey(accountName)) {
			certificatesService.verifyObjectWithSignature(accountName, accountName, signature);
			return new Account(accountName, accounts.get(accountName));
		} else {
			if (publicKey == null) {
				throw NOT_FOUND_EXCEPTION;
			}
			try {
				certificatesService.savePublicKey(accountName, publicKey);
				Account newAccount = new Account(accountName, 0L);

				accounts.put(newAccount.getName(), newAccount.getBalance());
				log.info("Create new account [account={}]", newAccount);

				syncService.addAccount(newAccount, signature, publicKey);

				return newAccount;
			} catch (Exception e) {
				log.error("Error during saving publicKey", e);
				throw INTERNAL_SERVER_EXCEPTION;
			}
		}
	}

	public void addAccounts(List<AccountDto> accountDtoList) {
		accountDtoList.forEach(account -> {
			if (!accounts.containsKey(account.name())) {
				try {
					certificatesService.savePublicKey(account.name(), account.pubKey());

					Account newAccount = new Account(account.name(), 0L);

					accounts.put(newAccount.getName(), newAccount.getBalance());
					log.info("Create new account from sync [account={}]", newAccount);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
