package ee.ciszewsj.cockroachcoin.data;

import org.springframework.http.HttpStatusCode;

public record ErrorData(HttpStatusCode code, String message) {
}
