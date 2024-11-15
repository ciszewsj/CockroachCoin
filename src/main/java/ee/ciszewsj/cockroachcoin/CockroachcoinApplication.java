package ee.ciszewsj.cockroachcoin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@SpringBootApplication
public class CockroachcoinApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CockroachcoinApplication.class);
		System.out.println(Arrays.toString(args));

		for (String argument: args) {
			if (argument.split("=")[0].equals("--node-mode")) {
				if ((argument.split("=")[1]).equals("INIT")) {
					application.setDefaultProperties(Collections.singletonMap("nodeMode", "INIT"));
					log.info("Starting in mode INIT node");
				} else {
					application.setDefaultProperties(Collections.singletonMap("nodeMode", "CHILD"));
					log.info("Starting as a child node");
				}
			}

			if (argument.split("=")[0].equals("--parent-node-address")) {
				String parentNodeAddress = (argument.split("=")[1]);
				application.setDefaultProperties(Collections.singletonMap("parentNodeAddress", parentNodeAddress));
				log.info("Parent address is " + parentNodeAddress);
			}
		}

		//	application.setDefaultProperties(Collections.singletonMap("server.port",8085));
		// if application.properties is present, then it takes precedence over the line above
		application.run(args);


	}


}
