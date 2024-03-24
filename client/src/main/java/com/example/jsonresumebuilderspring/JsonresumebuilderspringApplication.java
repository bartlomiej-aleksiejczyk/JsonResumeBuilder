package com.example.jsonresumebuilderspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JsonresumebuilderspringApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsonresumebuilderspringApplication.class, args);
	}


}
