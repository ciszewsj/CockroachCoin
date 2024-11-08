package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		return nodes.entrySet().stream()
				.map(entry -> new Node(entry.getKey(), entry.getValue()))
				.toList();
	}
}
