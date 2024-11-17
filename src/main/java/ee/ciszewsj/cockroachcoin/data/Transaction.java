package ee.ciszewsj.cockroachcoin.data;

public record  Transaction(
		long amount,
		String sender,
		String receiver,
		String signature,
		String prev_hash,
		long timestamp,
		TYPE type
) {
	public enum TYPE {
		GENESIS,
		DEPOSIT,
		TRANSFER,
	}
}
