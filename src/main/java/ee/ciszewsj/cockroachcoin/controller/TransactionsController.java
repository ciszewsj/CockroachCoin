package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.response.TransactionListResponse;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import ee.ciszewsj.cockroachcoin.service.CertificatesService;
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
	private final CertificatesService certificatesService;

	@PostMapping
	public void doTransaction(@RequestHeader("signature") String signature,
	                          @Valid @RequestBody TransactionRequest request) {
		log.debug("Request for do transaction [{}]", request);
		certificatesService.verifyObjectWithSignature(request.sender(), request, signature);
	}

}
