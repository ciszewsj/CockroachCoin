package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountsController {

	@PostMapping
	public Account getAccount(@RequestBody(required = false) String publicKey) {
		log.debug("Request for get account [publicKey={}]", publicKey);
		return new Account(0);
	}
}
