package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.*;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
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
	private final NodeService nodeService;

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
	public TransactionListResponse getTransactions() {
		log.debug("Request for get transactions");
		return new TransactionListResponse(transactionService.getTransactionList());
	}

	@PostMapping("/transactions")
	public void doTransaction(@RequestHeader("signature") String signature,
	                          @Valid @RequestBody TransactionRequest request) {
		log.debug("Request for do transaction [{}]", request);
		certificatesService.verifyObjectWithSignature(request.sender(), request, signature);
		transactionService.doTransaction(request.sender(), request.receiver(), request.amount(), signature);
	}

	@GetMapping("/nodes")
	public List<Node> getNodes() {
		log.debug("Request for get nodes");
		return nodeService.getNodeList();
	}

	@PostMapping("/nodes")
	public void postNode(@Valid @RequestBody Node request) {
		log.debug("Request for register new node [{}]", request);
		nodeService.registerNode(request);
	}
}
