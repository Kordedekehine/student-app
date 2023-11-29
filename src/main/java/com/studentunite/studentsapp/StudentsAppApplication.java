package com.studentunite.studentsapp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class StudentsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentsAppApplication.class, args);
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Student App")
						.description("API for Student, a social sharing site")
						.version("v1")
						.license(new License().name("Apache 2.0")));
	}
}
