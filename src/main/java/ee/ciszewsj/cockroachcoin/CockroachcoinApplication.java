package ee.ciszewsj.cockroachcoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CockroachcoinApplication {
public static int DIFFICULTY = 4;

	public static void main(String[] args) {
		SpringApplication.run(CockroachcoinApplication.class, args);
	}

}
