package ee.ciszewsj.cockroachcoin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationService {
	public final NodeService nodeService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Value("${server.port}")
	private String myPort;


	public void onNewBlock(BlockDto blockDto) {
		nodeService.getNodeList().forEach(
				node -> {
					log.info("propagating to: " + node.name() + " at " + node.url());
					try {
						HttpRequest request = HttpRequest.newBuilder()
								.uri(URI.create(node.url() + "/api/v1/block/new"))
								.header("Content-Type", "application/json")
								.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(blockDto)))
								.build();
						httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					} catch (IOException | InterruptedException e) {
						log.error("CONNECTION ERROR");
					}
				}
		);
	}

	public void onNewTransaction(Transaction transaction) {
		nodeService.getNodeList().forEach(
				node -> {
					try {
						HttpRequest request = HttpRequest.newBuilder()
								.uri(URI.create(node.url() + "/api/v1/transactions/new"))
								.header("Content-Type", "application/json")
								.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(transaction)))
								.build();
						httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					} catch (Exception e) {
						log.error("ERROR", e);
					}
				}
		);
	}

	public List<BlockDto> askForANewBlockchain() {

		List<BlockDto> to_return = List.of();
		List<Node> candidatesToTakeBlockchainFrom = nodeService.getNodeList();
		//remove my own url
		candidatesToTakeBlockchainFrom = candidatesToTakeBlockchainFrom.stream().filter(node -> !(node.url().equals(nodeService.getProperties().myUrl()))).toList();
		// take random one
		if (!candidatesToTakeBlockchainFrom.isEmpty()) {
			Random random = new Random();
			Node selectedNode = candidatesToTakeBlockchainFrom.get(random.nextInt(candidatesToTakeBlockchainFrom.size())); //

			// ask for a blockchain
			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(selectedNode.url() + "/api/v1/block"))
						.header("Content-Type", "application/json")
						.GET()
						.build();

				HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
				List<BlockDto> blockchainGotten = objectMapper.readValue(res.body(), new TypeReference<List<BlockDto>> () {});
//				log.info("Blockchain retrieved {}", blockchainGotten);

				to_return = blockchainGotten;
			} catch (Exception e) {
				log.error("ERROR", e);
			}

		}
		return to_return;
	}


	public void onBlockchainChange(List<BlockDto> blockChain) {
		nodeService.getNodeList().forEach(
				node -> {
					String myUrl = "http://localhost:" + myPort;
					if (!(node.url().equals(myUrl))) {
						try {
							HttpRequest request = HttpRequest.newBuilder()
									.uri(URI.create(node.url() + "/api/v1/block"))
									.header("Content-Type", "application/json")
									.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(blockChain)))
									.build();

							log.info("??? {}", objectMapper.writeValueAsString(request.uri()));
							httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
						} catch (Exception e) {
							log.error("ERROR", e);
						}
					}

				}
		);
	}
}
