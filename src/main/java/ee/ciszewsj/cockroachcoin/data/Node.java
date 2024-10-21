package ee.ciszewsj.cockroachcoin.data;

import jakarta.validation.constraints.NotEmpty;

public record Node(
		@NotEmpty String name,
		@NotEmpty String url,
		@NotEmpty String pubKey) {
}
