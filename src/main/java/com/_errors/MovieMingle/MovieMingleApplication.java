package com._errors.MovieMingle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieMingleApplication {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Ignoră câmpurile null
		return mapper;
	}


	public static void main(String[] args) {
		System.setProperty("com.sun.net.ssl.checkRevocation", "false");
		SpringApplication.run(MovieMingleApplication.class, args);
	}

}
