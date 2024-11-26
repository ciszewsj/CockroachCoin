package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.response.CreateNodeResponse;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.NodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {
	private final NodeService nodeService;
	private final BlockService blockService;

	@GetMapping
	public List<Node> nodes() {
		return nodeService.getNodeList();
	}

	@PostMapping("/greetings")
	public CreateNodeResponse handshake(@Valid @RequestBody Node request) {

		nodeService.registerNode(request);

		return new CreateNodeResponse(nodeService.getNodeList(), blockService.getBlockList());
	}

}
