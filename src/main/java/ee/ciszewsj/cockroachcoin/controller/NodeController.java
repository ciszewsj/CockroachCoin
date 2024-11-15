package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.configuration.HandshakeResponseMessage;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.request.JoinNetworkRequest;
import ee.ciszewsj.cockroachcoin.data.response.JoinNetworkResponse;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.Port;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController {
	private final NodeService nodeService;
	private final TransactionService transactionService;
	private final AccountRepository accountRepository;

	@GetMapping("/nodes")
	public List<Node> nodes() {
		return nodeService.getNodeList();
	}


	@PostMapping("/join_network")
	public JoinNetworkResponse registerNode(@RequestBody JoinNetworkRequest jnr, ServletRequest servletRequest) {
		String remoteAddress = servletRequest.getRemoteAddr();
		String remoteHost = servletRequest.getRemoteHost();;
		int remotePort = servletRequest.getRemotePort();

		String requestedName = jnr.name();
		String claimedAddress = jnr.address();

		log.info("Gotten join request from " + remoteAddress + " (host: " + remoteHost + " ; port: "+ remotePort);
		log.info("Node named: " + requestedName + "claims address " + claimedAddress);


		// now, check if nothing already has this name or address

		// later: add this node to a list of known nodes
		log.info("gotten join network request");
		return new JoinNetworkResponse(transactionService.getTransactionList(),nodeService.getNodeList()));

	}
}
