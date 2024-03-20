package com.inventory.app.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AppServerApplication {

	public static void main(String[] args) {
		log.info("Hooray! Media Server is ALIVE!");
		SpringApplication.run(AppServerApplication.class, args);
	}
}
