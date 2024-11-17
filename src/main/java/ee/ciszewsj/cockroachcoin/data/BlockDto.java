package ee.ciszewsj.cockroachcoin.data;

import java.util.List;

public record BlockDto(
		String index,
		List<Object> transactions,
		long timestamp,
		String previousHash
) {
}
