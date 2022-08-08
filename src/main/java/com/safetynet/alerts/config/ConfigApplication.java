package com.safetynet.alerts.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigApplication {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
