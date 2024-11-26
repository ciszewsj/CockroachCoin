package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.configuration.properites.BlockChainProperties;
import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.CommunicationService;
import ee.ciszewsj.cockroachcoin.service.GreetingsClient;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class InitialConfiguration {


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
	public List<BlockDto> blockListFromAnother(BlockChainProperties properties, GreetingsClient greetingsClient, NodeService nodeService) throws Exception {

		nodeService.registerNode(new Node("INIT", properties.connectUrl()));
		CreateNodeResponse response = greetingsClient.join_network(properties.connectUrl());

		List<Node> anotherNodes = response.nodeList().stream().filter(s -> !(s.url().equals(properties.myUrl()) || s.url().equals(properties.connectUrl()))).toList();
		for (Node next : anotherNodes) {
			try {
				greetingsClient.join_network(next.url());
				nodeService.registerNode(new Node(next.name(), next.url()));
			} catch (Exception e) {
				log.error("Could not register node [url={}]", next);
			}
		}
		return response.blocks();
	}
}
