package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.BlockChainProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreetingsClient {
	private final BlockChainProperties properties;

	public CreateNodeResponse join_network(String host) throws Exception {
		HttpClient httpClient = HttpClient.newHttpClient();
		String url = host + "/api/v1/nodes/greetings";

		String jsonRequest = Json.mapper().writeValueAsString(new Node(properties.myName(), properties.myUrl()));
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			CreateNodeResponse nodeResponse = Json.mapper().readValue(response.body(), CreateNodeResponse.class);
			log.info("Successfully read initial node [{}]", nodeResponse);
			return nodeResponse;
		} else {
			log.error("Could not get transactions");
			throw new IllegalStateException();
		}

	}
}
