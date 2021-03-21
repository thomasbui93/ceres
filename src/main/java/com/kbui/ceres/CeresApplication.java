package com.kbui.ceres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CeresApplication {
	public static void main(String[] args) {
		SpringApplication.run(CeresApplication.class, args);
	}
}
