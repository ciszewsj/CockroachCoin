package ee.ciszewsj.cockroachcoin.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
	private long balance;

	synchronized public void subAmount(long amount) {
		balance -= amount;
	}

	synchronized public void addAmount(long amount) {
		balance += amount;
	}
}
