package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.TransactionRequest;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import io.swagger.v3.oas.annotations.headers.Header;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BankController {
	private final TransactionService transactionService;
	private final AccountRepository accountRepository;

	@GetMapping
	public void greeting() {

	}

	@GetMapping("/accounts/{owner}")
	public Account getNodes(@PathVariable String owner,
	                        @RequestHeader("X-SECRET") String secret) {
		log.debug("Request for get nodes [owner={}]", owner);
		return accountRepository.findAccountWithAuthentication(owner, secret);
	}

	@PostMapping("/transactions")
	public void doTransaction(@Valid @RequestBody TransactionRequest request,
	                          @RequestHeader("X-SECRET") String secret) {
		log.debug("Request for do transaction [{}]", request);
		transactionService.doTransaction(request.sender(), request.receiver(), request.amount(), secret);
	}
}
