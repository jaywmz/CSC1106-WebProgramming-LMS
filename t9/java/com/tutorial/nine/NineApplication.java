package com.tutorial.nine;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NineApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(NineApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(NineApplication.class, args);
		LOGGER.info("Tutorial 9 - Spring Boot Application Started");

	}

}
