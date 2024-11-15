package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.data.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HandshakeResponseMessage {

		private int status;
		private String message;
		private List<Node> nodes;

	public HandshakeResponseMessage(int status, String message, List<Node> nodes) {
		this.status = status;
		this.message = message;
		this.nodes = nodes;
	}

}
