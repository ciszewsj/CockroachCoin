package ee.ciszewsj.cockroachcoin.data.response;

import ee.ciszewsj.cockroachcoin.data.AccountDto;
import ee.ciszewsj.cockroachcoin.data.Node;
import ee.ciszewsj.cockroachcoin.data.Transaction;

import java.util.List;

public record CreateNodeResponse(
		List<Transaction> transactionList,
		List<Node> nodeList,
		List<AccountDto> accountList
) {
}
