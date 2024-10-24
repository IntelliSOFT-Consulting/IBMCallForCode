package com.intellisoft.ibmcallforcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IbmcallforcodeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(IbmcallforcodeApplication.class, args);
	}
	
}
