package com.safetynet.alerts.model.dto;

import java.util.List;

import com.safetynet.alerts.mapper.PersonId;

import lombok.Data;

@Data
public class PersonDto {

	private PersonId id;
	
	private String address;
	
	private String city;
	
	private String zip;
	
	private String phone;
	
	private String email;
	
	private String birthdate;
	
	private List<MedicationDto> medications;
	
	private List<AllergyDto> allergies;
	
}
