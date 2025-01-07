package ee.ciszewsj.cockroachcoin.data.request;

import ee.ciszewsj.cockroachcoin.data.FromTransactionField;
import ee.ciszewsj.cockroachcoin.data.ToTransactionField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TransactionRequest(
		@NotEmpty long timestamp,
		@Valid List<FromTransactionRequest> senders,
		@Valid List<ToTransactionRequest> receivers
) {
	public record FromTransactionRequest(@NotEmpty String senderKey, @Min(0) long amount, String signature) {
		public FromTransactionField.CheckTransaction decode(int index, long timestamp, List<?> out) {
			return new FromTransactionField.CheckTransaction(index, timestamp, amount, out);
		}
	}

	public record ToTransactionRequest(@NotEmpty String senderKey, @Min(0) long amount) {
	}
}
