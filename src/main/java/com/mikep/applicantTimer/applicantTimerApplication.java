package com.mikep.applicantTimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class applicantTimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(applicantTimerApplication.class, args);
	}

}
