package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
	private final List<Node> nodeList = new ArrayList<>();


	public void registerNode(Node node) {
		nodeList.add(node);
	}
}
