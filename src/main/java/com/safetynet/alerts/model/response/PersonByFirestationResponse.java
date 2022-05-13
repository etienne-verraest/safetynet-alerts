package com.safetynet.alerts.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonByFirestationResponse {

	@JsonIgnore
	private PersonId id;
	
	private String firstName;
	private String lastName;
	
	// Birthdate is used to calculate age
	@JsonIgnore
	private String birthdate;
	
	private String address;
	private String phone;
	
	
}
