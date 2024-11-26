package ee.ciszewsj.cockroachcoin.data;

import jakarta.validation.constraints.NotEmpty;

public record Node(
		@NotEmpty String name,
		@NotEmpty String url
) {

	@Override
	public String toString() {
		return "Node (" + name + "); url: " + url;
	}
}
