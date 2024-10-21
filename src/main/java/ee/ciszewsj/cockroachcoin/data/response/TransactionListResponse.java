package ee.ciszewsj.cockroachcoin.data.response;

import ee.ciszewsj.cockroachcoin.data.Transaction;

import java.util.List;

public record TransactionListResponse(List<Transaction> transactions) {
}
