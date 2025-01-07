package ee.ciszewsj.cockroachcoin.data;

import java.util.List;

public record FromTransactionField(
		String senderKey,
		long amount,
		String signature
) {
	public CheckTransaction decode(int index, long timestamp, List<ToTransactionField> out) {
		return new CheckTransaction(index, timestamp, amount, out);
	}

	public record CheckTransaction(int index, long timestamp, long amount, List<?> out) {

	}
}
