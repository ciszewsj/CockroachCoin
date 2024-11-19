package ee.ciszewsj.cockroachcoin.data;

import jakarta.validation.constraints.NotEmpty;

public record Node(
		@NotEmpty String name,
		@NotEmpty String url
		// potentially more things in here, such as "isTrusted", how many blocks has it mined etc.

) {

	@Override
	public String toString() {
		return "Node (" + name + "); url: " + url;
	}
}
