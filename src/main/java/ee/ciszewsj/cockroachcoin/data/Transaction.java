package ee.ciszewsj.cockroachcoin.data;

public record Transaction(
		String sender,
		String receiver,
		long amount
) {
}
