package com.api.verificacion.api_verificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiVerificacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiVerificacionApplication.class, args);
	}

}
