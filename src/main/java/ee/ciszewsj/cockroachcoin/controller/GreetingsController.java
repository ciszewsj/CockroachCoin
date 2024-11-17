package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.AccountDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/greetings")
@RequiredArgsConstructor
public class GreetingsController {
	private final NodeService nodeService;
	private final TransactionService transactionService;

	@GetMapping
	public void greetings() {
		log.debug("Request for greetings");
	}

	@PostMapping
	public CreateNodeResponse handshake(@Valid @RequestBody Node request) {

		log.debug("Handshake request [request={}]", request);
		nodeService.registerNode(request);

		List<Node> nodeList = nodeService.getNodeList();
		List<Transaction> transactionList = transactionService.getTransactionList();
		return new CreateNodeResponse(transactionList, nodeList, new ArrayList<>());
	}
}
