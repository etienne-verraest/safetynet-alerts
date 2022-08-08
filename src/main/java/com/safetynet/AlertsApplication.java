package com.safetynet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.safetynet.alerts.service.DataPopulatorService;

@SpringBootApplication
public class AlertsApplication {

	@Autowired
	private DataPopulatorService dataPopulatorService;

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {
			dataPopulatorService.loadDatas();
		};
	}

}
