package ee.ciszewsj.cockroachcoin.data;

public record ToTransactionField(
		String publicKey,
		long amount
) {
}
