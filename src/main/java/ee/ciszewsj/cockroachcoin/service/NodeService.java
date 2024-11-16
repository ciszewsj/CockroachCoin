package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properties.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
	private final ArrayList<Node> nodes = new ArrayList<>();
	private final CertificatesFileStoreProperties properties;


	public boolean registerNode(Node node) {
		//check if it's not my name
		if (node.name().equals(properties.myName())) {
			return false;
		}

		// now, check if nothing already has this name or address
		for (Node existingNode: nodes) {
			if (node.address().equals(existingNode.address()) || (node.name().equals(existingNode.name())) ) {
				log.warn("Cannot register node, name or address already taken.");
				return false;
			}
		}

		nodes.add(new Node(node.name(), node.address()));
		log.info("Node register successfully [node={}]", node);
		return true;
	}

	public ArrayList<Node> getNodeList() {
		log.info("showing node list");
		return nodes;
	}
}
