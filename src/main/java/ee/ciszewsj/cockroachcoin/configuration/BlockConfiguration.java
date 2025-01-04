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
	public BlockService blockService(List<BlockDto> blockList, CommunicationService communicationService, AccountService accountService) {
		return new BlockService(blockList, communicationService, accountService);
	}

	@Bean
	@ConditionalOnProperty(prefix = "certificates", value = "connect-url", matchIfMissing = true)
	public List<BlockDto> blockList() {
		BlockDto dto = new BlockDto(0, List.of(), 0, 0, "");
		List<BlockDto> a = new ArrayList<>();
		a.add(dto);
		return a;
	}

	@Bean
	@ConditionalOnProperty(prefix = "certificates", value = "connect-url")
	public List<BlockDto> blockListFromAnother(CertificatesFileStoreProperties properties, NodeService nodeService) throws IOException, InterruptedException {
		Node node = new Node(properties.myName(), properties.myUrl());
		GreetingsRequest greetingsRequest = new GreetingsRequest(node);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(greetingsRequest);
		log.info("Co tutaj wysyłam {}", requestBody);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(properties.connectUrl() + "/api/v1/node"))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		log.info("STATUS GET_BLOCKCHAIN ? {}", response.statusCode());

		GreetingsResponse greetingsResponse = objectMapper.readValue(response.body(), GreetingsResponse.class);
		List<Node> possibleValues = greetingsResponse.others();
		possibleValues = possibleValues.stream().filter(s -> !s.url().equals(properties.myUrl())).toList();
		if (!possibleValues.isEmpty()) {
			Random random = new Random();
			Node selectedNode = possibleValues.get(random.nextInt(possibleValues.size()));
			request = HttpRequest.newBuilder()
					.uri(URI.create(selectedNode.url() + "/api/v1/node"))
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();
			HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			nodeService.register(selectedNode);
		}


		nodeService.register(greetingsResponse.node());


		log.info("Successfully greeting [{}]", greetingsResponse);
		return greetingsResponse.blockChain();
	}
}
