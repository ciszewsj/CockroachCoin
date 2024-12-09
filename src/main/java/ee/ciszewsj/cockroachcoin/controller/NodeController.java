package ee.ciszewsj.cockroachcoin.controller;


import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.request.GreetingsRequest;
import ee.ciszewsj.cockroachcoin.data.response.GreetingsResponse;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/node")
@RequiredArgsConstructor
public class NodeController {
	private final NodeService nodeService;
	private final BlockService blockService;
	private final CertificatesFileStoreProperties properties;

	@GetMapping
	public List<Node> nodeList() {
		return nodeService.getNodeList();
	}

	@PostMapping
	public GreetingsResponse greetings(@RequestBody GreetingsRequest request) {
		var nodes = nodeService.getNodes();
		nodeService.register(request.node());
		log.info("Greetings with [{}]", request);
		return new GreetingsResponse(blockService.getBlockList(), nodes, new Node(properties.myName(), properties.myUrl()));
	}
}
