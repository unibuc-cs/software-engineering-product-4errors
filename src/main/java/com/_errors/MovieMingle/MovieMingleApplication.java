package com._errors.MovieMingle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieMingleApplication {

	public static void main(String[] args) {
		System.setProperty("com.sun.net.ssl.checkRevocation", "false");
		SpringApplication.run(MovieMingleApplication.class, args);
	}

}
