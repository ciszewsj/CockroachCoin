package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properties.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
	private final Map<String, String> nodes = new HashMap<>();
	private final CertificatesFileStoreProperties properties;


	public void registerNode(Node node) {
		if (node.name().equals(properties.myName())) {
			return;
		}
		nodes.put(node.name(), node.url());
		log.info("Node register successfully [node={}]", node);
	}

	public List<Node> getNodeList() {
		log.info("showing node list");
		return nodes.entrySet().stream()
				.map(entry -> new Node(entry.getKey(), entry.getValue()))
				.toList();
	}
}
