package com.example.springbatchtuto;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchTutoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchTutoApplication.class, args);
	}

}
