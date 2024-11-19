package ee.ciszewsj.cockroachcoin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationService {
	private final NodeService nodeService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Value("${config.name}")
	private String myName;
	@Value("${server.port}")
	private String myPort;



	public void onBlockChange(List<BlockDto> blockChain) {
		nodeService.getNodeList().forEach(
				node -> {
					String myUrl = "http://localhost:"+myPort;
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
