package com.safetynet.alerts.model.dto;

import com.safetynet.alerts.model.Person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {
	
	private Person person;
	
	private String namePosology;

}
