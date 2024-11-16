package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.AccountDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.response.JoinNetworkResponse;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/greetings")
@RequiredArgsConstructor
public class GreetingsController {
	private final NodeService nodeService;
	private final TransactionService transactionService;
	private final AccountRepository accountRepository;

	@GetMapping
	public ResponseEntity<String> greetings() {
		log.debug("Request for greetings");
		return ResponseEntity.ok("Hello, welcome.");
	}

	@PostMapping
	public JoinNetworkResponse handshake(@Valid @RequestBody Node request) {

		log.debug("Handshake request [request={}]", request);
		nodeService.registerNode(request);

		List<Node> nodeList = nodeService.getNodeList();
		List<Transaction> transactionList = transactionService.getTransactionList();
		return new JoinNetworkResponse(transactionList, nodeList);
	}
}
