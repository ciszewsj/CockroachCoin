package ee.ciszewsj.cockroachcoin.data.response;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.Node;

import java.util.List;

public record CreateNodeResponse(
		List<Node> nodeList,
		List<BlockDto> blocks
) {
}
