package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Account;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {
	private final NodeService nodeService;

	ExecutorService executorService = Executors.newFixedThreadPool(8);

	public void addAccount(Account account, String signature, String publicKey) {
		nodeService.getNodeList().forEach(node ->
				executorService.submit(() -> {
					HttpClient httpClient = HttpClient.newHttpClient();
					HttpRequest request;
					try {
						request = HttpRequest.newBuilder()
								.uri(new URI(node.url() + "/api/v1/accounts/" + account.getName()))
								.header("Content-Type", "application/json")
								.headers("signature", signature)
								.POST(HttpRequest.BodyPublishers.ofString(publicKey))
								.build();
						HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
						log.info("Account added successfully [node={}]", node);
					} catch (IOException | InterruptedException | URISyntaxException e) {
						throw new RuntimeException(e);
					}
				})
		);
	}

	public void syncTransaction(Transaction transaction) {

	}
}
