package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.TransactionListResponse;
import ee.ciszewsj.cockroachcoin.data.TransactionRequest;
import ee.ciszewsj.cockroachcoin.service.AccountRepository;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
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
	private final Clock clock;

	public NodeInitializer(CertificatesFileStoreProperties properties,
	                       TransactionService transactionService,
	                       CertificatesService certificatesService,
	                       AccountRepository accountRepository,
	                       Clock clock) throws IOException, InterruptedException {
		this.properties = properties;
		this.transactionService = transactionService;
		this.certificatesService = certificatesService;
		this.accountRepository = accountRepository;
		this.clock = clock;

		if (properties.connectUrl() == null || properties.connectUrl().isEmpty()) {
			initFirstTransaction();
		} else {
			getTransactions();
		}
		recalculateTransactions();
	}

	private void connectToNodes() {
		
	}

	private void getTransactions() throws IOException, InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();

		String url = properties.connectUrl() + "/api/v1/transactions";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			TransactionListResponse transactionListResponse = Json.mapper().readValue(response.body(), TransactionListResponse.class);
			log.info("Successfully read transactions from initial node [{}]", transactionListResponse);
			transactionService.addTransaction(transactionListResponse.transactions());

		} else {
			log.error("Could not get transactions");
			throw new IllegalStateException("Could not download transactions");
		}
	}

	private void initFirstTransaction() {
		transactionService.addTransaction(List.of(new Transaction(0, null, null, null, null, clock.millis(), Transaction.TYPE.GENESIS)));
		transactionService.doDeposit("acc1", 10000);
		transactionService.doDeposit("acc2", 10000);
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
				log.warn("Could not validated transaction [{}]", transaction, e);
			}
			if (isValid) {
				accountRepository.findAccount(transaction.sender()).subAmount(transaction.amount());
				accountRepository.findAccount(transaction.receiver()).addAmount(transaction.amount());
			}
		}
	}
}
