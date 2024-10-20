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

	public final static HttpClientErrorException NOT_FOUND_EXCEPTION = new HttpClientErrorException(HttpStatus.NOT_FOUND);
	public final static HttpClientErrorException UNAUTHORIZED_EXCEPTION = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
	public final static HttpClientErrorException TRANSACTION_OVER_LIMIT_EXCEPTION = new HttpClientErrorException(HttpStatus.CONFLICT, "Transaction over limit");
	public final static HttpClientErrorException INTERNAL_SERVER_EXCEPTION = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorData> handleHttpClientErrorException(HttpClientErrorException ex) {
		HttpStatusCode status = ex.getStatusCode();
		String message = ex.getMessage();
		return ResponseEntity.status(status).body(new ErrorData(status, message));
	}
}
