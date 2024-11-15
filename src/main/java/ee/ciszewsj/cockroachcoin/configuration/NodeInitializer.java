package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.configuration.properties.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import ee.ciszewsj.cockroachcoin.data.response.JoinNetworkResponse;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.List;

@Slf4j
@Service
public class NodeInitializer {
	private final CertificatesFileStoreProperties properties;
	private final TransactionService transactionService;
	private final CertificatesService certificatesService;
	private final AccountRepository accountRepository;
	private final NodeService nodeService;
	private final Clock clock;

	public NodeInitializer(CertificatesFileStoreProperties properties,
                           TransactionService transactionService,
                           CertificatesService certificatesService,
                           AccountRepository accountRepository,
                           Clock clock,
                           NodeService nodeService
	) throws IOException, InterruptedException {
		this.properties = properties;
		this.transactionService = transactionService;
		this.certificatesService = certificatesService;
		this.accountRepository = accountRepository;
		this.nodeService = nodeService;
		this.clock = clock;

        if (properties.connectUrl() == null || properties.connectUrl().isEmpty()) {
			initFirstTransaction();
		} else {
			getTransactions();
		}
		recalculateTransactions();
	}

	private void getTransactions() throws IOException, InterruptedException {
		nodeService.registerNode(new Node("BASE", properties.connectUrl()));

		HttpClient httpClient = HttpClient.newHttpClient();
		String url = properties.connectUrl() + "/api/v1/greetings";
		Node thisNode = new Node(properties.myName(), properties.myUrl());
		String jsonRequest = Json.mapper().writeValueAsString(thisNode);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			JoinNetworkResponse nodeResponse = Json.mapper().readValue(response.body(), JoinNetworkResponse.class);
			log.info("Successfully read transactions from initial node [{}]", nodeResponse);
			transactionService.addTransaction(nodeResponse.transactionList());
			accountRepository.addAccounts(nodeResponse.accountList());
			for (Node node : nodeResponse.nodeList()) {
				nodeService.registerNode(node);
			}
		} else {
			log.error("Could not get transactions");
			throw new IllegalStateException("Could not download transactions");
		}
	}

	private void initFirstTransaction() {
		transactionService.addTransaction(List.of(new Transaction(0, null, null, null, null, clock.millis(), Transaction.TYPE.GENESIS)));
	}

	private void recalculateTransactions() {
		for (Transaction transaction : transactionService.getTransactionList()) {
			if (transaction.type() == Transaction.TYPE.GENESIS) {
				continue;
			} else if (transaction.type() == Transaction.TYPE.DEPOSIT) {
				accountRepository.findAccount(transaction.receiver()).setBalance(transaction.amount());
				continue;
			}
			boolean isValid = false;
			try {
				certificatesService.verifyObjectWithSignature(transaction.sender(), new TransactionRequest(transaction.sender(), transaction.receiver(), transaction.amount()), transaction.signature());
				isValid = true;
			} catch (Exception e) {
				log.warn("Could not validate transaction [{}]", transaction, e);
			}
			if (isValid) {
				accountRepository.findAccount(transaction.sender()).subAmount(transaction.amount());
				accountRepository.findAccount(transaction.receiver()).addAmount(transaction.amount());
			}
		}
	}

}

