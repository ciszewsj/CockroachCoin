package ee.ciszewsj.cockroachcoin.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
	private final String name;
	private final String secret;
	private long balance;
}
