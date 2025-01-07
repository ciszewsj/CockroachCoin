package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountsController {

	private final AccountService accountService;

	@PostMapping
	public Account getAccount(@RequestBody(required = false) String publicKey) {
		log.info("Request for get account [senderKey={}, balance={}]", publicKey, accountService.getBalance(publicKey));
		return new Account(accountService.getBalance(publicKey));
	}
}
