package ee.ciszewsj.cockroachcoin.data;

public record FromTransactionField(
		String publicKey,
		long amount,
		String signature
) {
}
