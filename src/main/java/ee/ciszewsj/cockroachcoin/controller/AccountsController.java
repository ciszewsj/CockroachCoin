package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.AccountDto;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountsController {
	private final AccountRepository accountRepository;
	
	@GetMapping
	public Account getAccount(@RequestBody(required = false) String publicKey) {
		log.debug("Request for get account [publicKey={}]", publicKey);
		return accountRepository.findAccount(publicKey);
	}
}
