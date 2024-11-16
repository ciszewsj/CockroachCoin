package ee.ciszewsj.cockroachcoin.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record Node(
		@Valid
		@NotEmpty String name,
		@Valid
		@NotEmpty String address
) {
}
