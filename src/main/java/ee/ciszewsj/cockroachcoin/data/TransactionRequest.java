package ee.ciszewsj.cockroachcoin.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record TransactionRequest(
		@NotEmpty String sender,
		@NotEmpty String receiver,
		@Min(0) long amount
) {
	public String sender() {
		return sender.toLowerCase();
	}

	public String receiver() {
		return receiver.toLowerCase();
	}
}
