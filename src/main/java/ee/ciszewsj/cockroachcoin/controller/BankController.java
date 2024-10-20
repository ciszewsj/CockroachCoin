package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.TransactionRequest;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BankController {
	private final TransactionService transactionService;
	private final AccountRepository accountRepository;
	private final CertificatesService certificatesService;

	@GetMapping
	public void greetings() {
		log.debug("Request for greetings");
	}

	@GetMapping("/accounts")
	public List<Account> getAccounts() {
		log.debug("Request for get accounts");
		return accountRepository.getAccountList();
	}

	@GetMapping("/accounts/{owner}")
	public Account getNodes(@RequestHeader("signature") String signature,
	                        @PathVariable String owner) {
		log.debug("Request for get account [owner={}]", owner);
		certificatesService.verifyObjectWithSignature(owner, owner, signature);
		return accountRepository.findAccount(owner);
	}

	@GetMapping("/transactions")
	public List<Transaction> getTransactions() {
		log.debug("Request for get transactions");
		return transactionService.getTransactionList();
	}

	@PostMapping("/transactions")
	public void doTransaction(@RequestHeader("signature") String signature,
	                          @Valid @RequestBody TransactionRequest request) {
		log.debug("Request for do transaction [{}]", request);
		certificatesService.verifyObjectWithSignature(request.sender(), request, signature);
		transactionService.doTransaction(request.sender(), request.receiver(), request.amount(), signature);
	}
}
