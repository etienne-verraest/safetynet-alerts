package com.safetynet.alerts.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonByStationNumberDto {

	private PersonId id;
	
	// Birthdate is used to calculate age
	@JsonIgnore()
	private String birthdate;
	
	private String address;
	private String phone;
	
	
}
