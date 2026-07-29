package com.rastreador.rastreador_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RastreadorProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(RastreadorProductosApplication.class, args);
	}

}
