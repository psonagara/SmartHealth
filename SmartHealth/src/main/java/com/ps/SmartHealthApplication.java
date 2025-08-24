package com.ps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartHealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHealthApplication.class, args);
	}

}
