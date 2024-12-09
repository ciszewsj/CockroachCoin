package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.data.FromTransactionField;
import ee.ciszewsj.cockroachcoin.data.ToTransactionField;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final Clock clock;
	private final AccountService accountService;
	private final BlockService blockService;
	private final MinerService minerService;

	public void doTransaction(TransactionRequest request) {

		if (blockService.getBlockList().size() < 2) {
			throw new IllegalStateException("COULD NOT MAKE TRANSACTION YET");
		}

		long inTransaction = request.senders().stream().mapToLong(TransactionRequest.FromTransactionRequest::amount).sum();
		long outTransaction = request.receivers().stream().mapToLong(TransactionRequest.ToTransactionRequest::amount).sum();

		if (inTransaction != outTransaction) {
			throw new IllegalStateException("IN != OUT");
		}

		BlockDto block = blockService.getLast();
		Transaction lastTransaction = block.transactions().getLast();

		List<FromTransactionField> from = request.senders().stream().map(
				sender -> new FromTransactionField(sender.senderKey(), sender.amount(), sender.signature())
		).toList();

		List<ToTransactionField> to = request.receivers().stream().map(
				receiver -> new ToTransactionField(receiver.senderKey(), receiver.amount())
		).toList();

		Transaction newTransaction = new Transaction(lastTransaction.index() + 1, from, to, lastTransaction.calculateHash(), clock.millis(), Transaction.TYPE.TRANSFER);
		accountService.doTransaction(newTransaction);
		block.transactions().add(newTransaction);
		minerService.listUpdated();
		log.info("Successful do transaction [{}]", request);

	}

	public void newTransaction(Transaction transaction) {

	}

}
