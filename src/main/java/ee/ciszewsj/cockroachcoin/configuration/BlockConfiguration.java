package ee.ciszewsj.cockroachcoin.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.CommunicationService;
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

@Slf4j
@Configuration
public class BlockConfiguration {

	@Bean
	public BlockService blockService(List<BlockDto> blockList, CommunicationService communicationService) {
		return new BlockService(blockList, communicationService);
	}

	@Bean
	@ConditionalOnProperty(prefix = "certificates", value = "connect-url", matchIfMissing = true)
	public List<BlockDto> blockList(Clock clock) {
		BlockDto dto = new BlockDto(0, new ArrayList<>(), clock.millis(), 0, "");
		List<BlockDto> a = new ArrayList<>();
		a.add(dto);
		return a;
	}

	@Bean
	@ConditionalOnProperty(prefix = "certificates", value = "connect-url")
	public List<BlockDto> blockListFromAnother(CertificatesFileStoreProperties properties) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(properties.connectUrl() + "/api/v1/block"))
				.header("Accept", "application/json")
				.GET()
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		ObjectMapper objectMapper = new ObjectMapper();
		log.info("STATUS GET_BLOCKCHAIN ? {}", response.statusCode());
		return objectMapper.readValue(response.body(), new TypeReference<>() {
		});
	}
}
