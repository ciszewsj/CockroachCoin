package ee.ciszewsj.cockroachcoin.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.request.GreetingsRequest;
import ee.ciszewsj.cockroachcoin.data.response.GreetingsResponse;
import ee.ciszewsj.cockroachcoin.service.AccountService;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.CommunicationService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
public class BlockConfiguration {

	@Bean
	public BlockService blockService(List<BlockDto> blockList, CommunicationService communicationService, AccountService accountService, CertificatesFileStoreProperties properties) {
		return new BlockService(blockList, communicationService, accountService, properties);
	}

	@Bean
	@ConditionalOnProperty(prefix = "certificates", value = "connect-url", matchIfMissing = true)
	public List<BlockDto> blockList(CertificatesFileStoreProperties properties, NodeService nodeService) throws IOException, InterruptedException {
		// MANUALLY CHECKING IF properties.connectUrl property is present. If not, then I am genesis
		if (!(properties.connectUrl() != null && !properties.connectUrl().isEmpty())) {
			log.info("i am the genesis");
			BlockDto dto = new BlockDto(0, List.of(), 0, 0, "");
			List<BlockDto> a = new ArrayList<>();
			a.add(dto);
			return a;
		} else {
			log.info("i am NOT the genesis");
			Node node = new Node(properties.myName(), properties.myUrl());
			GreetingsRequest greetingsRequest = new GreetingsRequest(node);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(greetingsRequest);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(properties.connectUrl() + "/api/v1/node"))
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			GreetingsResponse greetingsResponse = objectMapper.readValue(response.body(), GreetingsResponse.class);

			List<Node> possibleValues = greetingsResponse.others();
			possibleValues = possibleValues.stream().filter(s -> !s.url().equals(properties.myUrl())).toList(); // node removes its own address
			if (!possibleValues.isEmpty()) {
				Random random = new Random();
				Node selectedNode = possibleValues.get(random.nextInt(possibleValues.size())); // picks one random node from the list and sends a register request there
				request = HttpRequest.newBuilder()
						.uri(URI.create(selectedNode.url() + "/api/v1/node"))
						.header("Accept", "application/json")
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(requestBody))
						.build();
				HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				nodeService.register(selectedNode);
			}

			log.info("Received blockchain: {}", greetingsResponse.blockChain());
			nodeService.register(greetingsResponse.node());

			return greetingsResponse.blockChain();

		}
	}

//	@Bean
//	@ConditionalOnProperty(prefix = "certificates", value = "connect-url") //fired only if "connect-url" property is present
//	public List<BlockDto> blockListFromAnother(CertificatesFileStoreProperties properties, NodeService nodeService) throws IOException, InterruptedException {
//		log.info("i am NOT the genesis");
//		Node node = new Node(properties.myName(), properties.myUrl());
//		GreetingsRequest greetingsRequest = new GreetingsRequest(node);
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		String requestBody = objectMapper.writeValueAsString(greetingsRequest);
////		log.info("Co tutaj wysyłam {}", requestBody);
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(properties.connectUrl() + "/api/v1/node"))
//				.header("Accept", "application/json")
//				.header("Content-Type", "application/json")
//				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
//				.build();
//
//		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//		log.info("STATUS GET_BLOCKCHAIN ? {}", response.statusCode());
//		log.info(response.body());
//
//		GreetingsResponse greetingsResponse = objectMapper.readValue(response.body(), GreetingsResponse.class);
//		log.info("greetings blockchain {}",greetingsResponse.blockChain().toString());
//
//		List<Node> possibleValues = greetingsResponse.others();
//		possibleValues = possibleValues.stream().filter(s -> !s.url().equals(properties.myUrl())).toList(); // node removes its own address
//		if (!possibleValues.isEmpty()) {
//			Random random = new Random();
//			Node selectedNode = possibleValues.get(random.nextInt(possibleValues.size())); // picks one random node from the list and sends a register request there
//			request = HttpRequest.newBuilder()
//					.uri(URI.create(selectedNode.url() + "/api/v1/node"))
//					.header("Accept", "application/json")
//					.header("Content-Type", "application/json")
//					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
//					.build();
//			HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//			nodeService.register(selectedNode);
//		}
//
//
//		nodeService.register(greetingsResponse.node());
//
//
//		log.info("Successfully greeting [{}]", greetingsResponse);
//
//		log.info("Gotten blockchain {}", greetingsResponse.blockChain().toString());
//
//		return greetingsResponse.blockChain();
//	}
}
