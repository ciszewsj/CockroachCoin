package ee.ciszewsj.cockroachcoin.data;

import java.util.List;

public record TransactionListResponse(List<Transaction> transactions) {
}
