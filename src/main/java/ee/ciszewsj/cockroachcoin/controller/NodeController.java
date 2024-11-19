package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.configuration.NodeInitializer;
import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {
	private final CertificatesFileStoreProperties properties;
	private final NodeService nodeService;
	private final NodeInitializer nodeInitializer;
	private final TransactionService transactionService;

	public boolean hasJoinedNetwork=false;

	@Value("${config.isInit:false}")
	private boolean isInitNode;
	@Value("${config.connectToUrl}")
	private String connectToUrl;
	@Value("${server.port}")
	private String myPort;
	@Value("${config.name}")
	private String myName;

	@GetMapping
	public ArrayList<Node> nodes() {
		return nodeService.getNodeList();
	}

	@PostMapping("/greetings")
	public CreateNodeResponse handshake(@Valid @RequestBody Node request, ServletRequest servletRequest) {
		log.info("Handshake request.");
		log.info(request.toString());
		// somewhat validate the request
		if ( request.name()==null || request.name().isBlank()
				|| request.url()==null || request.url().isBlank() ) {
			return new CreateNodeResponse(null, null, null);
		}

		String requestedName = request.name();
		String claimedUrl = request.url();

		String remoteAddress = servletRequest.getRemoteAddr();
		String remoteHost = servletRequest.getRemoteHost();
		int remotePort = servletRequest.getRemotePort();

		log.info("Gotten join request from " + remoteAddress + " (host: " + remoteHost + " ; port: "+ remotePort);
		log.info("Node named: " + requestedName + " ; claims address " + claimedUrl);

		nodeService.registerNode(request);


		ArrayList<Node> nodeList = nodeService.getNodeList();
		List<Transaction> transactionList = transactionService.getTransactionList();
		return new CreateNodeResponse(transactionList, nodeList, new ArrayList<>());
	}

	//used for making the requesting node join the network
	@PostMapping("/join_network")
	public ResponseEntity<Object> join_network(ServletRequest servletRequest) throws IOException, InterruptedException {
		if (hasJoinedNetwork)
			return new ResponseEntity<Object>("Node has already joined network", HttpStatus.FORBIDDEN);
		if (isInitNode) {
			log.info("Joining the network as an INIT node.");
			hasJoinedNetwork=true;
			nodeInitializer.initFirstTransactionAndInitialize();
			nodeInitializer.recalculateTransactions();
			return new ResponseEntity<>(HttpStatus.OK);
		} else { //
			HttpClient httpClient = HttpClient.newHttpClient();
//		String url = properties.connectUrl() + "/api/v1/nodes/greetings";
			String url = connectToUrl + "/api/v1/nodes/greetings";
			;
			String myUrl = "http://localhost:" + myPort;
			log.info("Connecting to url: " + url);
			Node thisNode = new Node(myName, myUrl);
			String jsonRequest = Json.mapper().writeValueAsString(thisNode);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				CreateNodeResponse nodeResponse = Json.mapper().readValue(response.body(), CreateNodeResponse.class);
				log.info("Successfully read transactions from initial node [{}]", nodeResponse);

				transactionService.addTransactionList(nodeResponse.transactionList());
				nodeService.setAsKnownNodes(nodeResponse.nodeList());

				hasJoinedNetwork = true;
				return new ResponseEntity<Object>(HttpStatus.OK);
			} else {
				log.error("Could not get transactions");
//			throw new IllegalStateException("Could not download transactions");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
	}
}
