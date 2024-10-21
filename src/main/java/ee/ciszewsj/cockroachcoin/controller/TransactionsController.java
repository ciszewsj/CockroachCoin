package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.response.TransactionListResponse;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
import ee.ciszewsj.cockroachcoin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {
	private final TransactionService transactionService;
	private final CertificatesService certificatesService;

	@GetMapping
	public TransactionListResponse getTransactions() {
		log.debug("Request for get transactions");
		return new TransactionListResponse(transactionService.getTransactionList());
	}

	@PostMapping
	public void doTransaction(@RequestHeader("signature") String signature,
	                          @Valid @RequestBody TransactionRequest request) {
		log.debug("Request for do transaction [{}]", request);
		certificatesService.verifyObjectWithSignature(request.sender(), request, signature);
		transactionService.doTransaction(request.sender(), request.receiver(), request.amount(), signature);
	}

}
