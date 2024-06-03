package webprogramming.csc1106;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Csc1106Application {

	private static final Logger log = LoggerFactory.getLogger(Csc1106Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Csc1106Application.class, args);
		log.info("Application started");
	}

}
