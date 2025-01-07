package ee.ciszewsj.cockroachcoin.data;

public record ToTransactionField(
		String senderKey,
		long amount
) {
}
