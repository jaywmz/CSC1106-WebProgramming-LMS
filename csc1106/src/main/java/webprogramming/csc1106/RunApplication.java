package webprogramming.csc1106;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RunApplication {

	private static final Logger log = LoggerFactory.getLogger(RunApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RunApplication.class, args);
		log.info("Application started");
	}

}
