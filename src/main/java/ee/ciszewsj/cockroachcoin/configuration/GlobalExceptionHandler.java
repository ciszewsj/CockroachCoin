package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.data.ErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	public static HttpClientErrorException NOT_FOUND_EXCEPTION = new HttpClientErrorException(HttpStatus.NOT_FOUND);
	public static HttpClientErrorException TRANSACTION_OVER_LIMIT_EXCEPTION = new HttpClientErrorException(HttpStatus.CONFLICT, "Transaction over limit");

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorData> handleHttpClientErrorException(HttpClientErrorException ex) {
		HttpStatusCode status = ex.getStatusCode();
		String message = ex.getMessage();
		return ResponseEntity.status(status).body(new ErrorData(status, message));
	}
}
