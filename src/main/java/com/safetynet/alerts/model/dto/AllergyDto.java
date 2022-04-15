package com.safetynet.alerts.model.dto;

import lombok.Data;

@Data
public class AllergyDto {

	private String id;
	
	private String name;
	
	private PersonDto person;
	
}
