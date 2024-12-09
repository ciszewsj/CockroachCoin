package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
	private final ArrayList<Node> nodes = new ArrayList<>();
	private final CertificatesFileStoreProperties properties;

	public ArrayList<Node> getNodeList() {
		return nodes;
	}
}
