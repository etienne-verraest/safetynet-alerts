package com.safetynet.alerts.model.dto;

import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergyDto {
	
	private PersonId person;
	
	private String name;

}
