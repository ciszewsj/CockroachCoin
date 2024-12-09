package ee.ciszewsj.cockroachcoin.data.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TransactionRequest(
		@Valid List<FromTransactionRequest> senders,
		@Valid List<ToTransactionRequest> receivers
) {
	public record FromTransactionRequest(@NotEmpty String senderKey, @Min(0) long amount, String signature) {
	}

	public record ToTransactionRequest(@NotEmpty String senderKey, @Min(0) long amount) {
	}
}
