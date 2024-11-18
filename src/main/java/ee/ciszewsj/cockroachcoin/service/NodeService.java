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

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
	private final ArrayList<Node> nodes = new ArrayList<>();
	private final CertificatesFileStoreProperties properties;

	public void registerNode(Node node) {
		if (node.name().equals(properties.myName())) {
			return;
		}
		for (Node existingNode: nodes) {
			if (existingNode.name().equals(node.name())
					|| existingNode.url().equals(node.url())) {
				log.warn("Name or url already taken, cannot register node");
				return;
			}

		}

		nodes.add(new Node(node.name(), node.url()));
		log.info("Node register successfully [node={}]", node);
	}

	public ArrayList<Node> getNodeList() {
		return nodes;
	}
}
