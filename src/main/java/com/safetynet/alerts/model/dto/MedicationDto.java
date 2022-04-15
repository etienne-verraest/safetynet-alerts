package com.safetynet.alerts.model.dto;

import lombok.Data;

@Data
public class MedicationDto {

	private Integer id;
	
	private String namePosology;
	
	private PersonDto person;
	
}
