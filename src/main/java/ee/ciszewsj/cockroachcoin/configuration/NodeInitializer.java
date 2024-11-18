package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NodeInitializer {
	private final CertificatesFileStoreProperties properties;
	private final TransactionService transactionService;
	private final CertificatesService certificatesService;
	private final NodeService nodeService;
	private final Clock clock;

	@Value("${config.isInit:false}")
	private boolean isInitNode;

	@Value("${server.port}")
	private String myPort;
	@Value("${config.name}")
	private String myName;

	public NodeInitializer(CertificatesFileStoreProperties properties,
	                       TransactionService transactionService,
	                       CertificatesService certificatesService,
	                       Clock clock,
	                       NodeService nodeService) throws IOException, InterruptedException {
		this.properties = properties;
		this.transactionService = transactionService;
		this.certificatesService = certificatesService;
		this.nodeService = nodeService;
		this.clock = clock;


		// this check will not work, because the value is injected after the constructor
//		if (isInitNode) {
//			log.info("Starting as INIT node, initializing first transaction");
//			initFirstTransactionAndInitialize();
//		} else {
//			log.info("Starting as NOT an INIT node");
////			getTransactions();
//		}
		recalculateTransactions();
	}


	public void getTransactions() throws IOException, InterruptedException {
		if (isInitNode) {
			nodeService.registerNode(new Node("BASE", properties.connectUrl()));
		}

		HttpClient httpClient = HttpClient.newHttpClient();
		String url = properties.connectUrl() + "/api/v1/nodes/greetings";
		String myUrl = "http://localhost:" + myPort;
		Node thisNode = new Node(properties.myName(), properties.myUrl());
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
			for (Node node : nodeResponse.nodeList()) {
				nodeService.registerNode(node);
			}
		} else {
			log.error("Could not get transactions");
			throw new IllegalStateException("Could not download transactions");
		}
	}

	public void initFirstTransactionAndInitialize() {
		ArrayList<Node> firstNodes = new ArrayList<>();
		firstNodes.add(new Node("INIT", "http://localhost:"+myPort));
		nodeService.setAsKnownNodes(firstNodes);
		transactionService.addTransactionList(List.of(new Transaction(0, null, null, null, null, clock.millis(), Transaction.TYPE.GENESIS)));
	}

	public void recalculateTransactions() {
		for (Transaction transaction : transactionService.getTransactionList()) {
			if (transaction.type() == Transaction.TYPE.GENESIS) {
				continue;
			} else if (transaction.type() == Transaction.TYPE.DEPOSIT) {
				continue;
			}
			boolean isValid = false;
			try {
				certificatesService.verifyObjectWithSignature(transaction.sender(), new TransactionRequest(transaction.sender(), transaction.receiver(), transaction.amount()), transaction.signature());
				isValid = true;
			} catch (Exception e) {
				log.warn("Could not validated transaction [{}]", transaction, e);
			}
			if (isValid) {
			}
		}
	}
}
