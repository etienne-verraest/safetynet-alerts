package com.safetynet.alerts.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildrenResponse {

	@JsonIgnore()
	private PersonId id;
	
	private String firstName;
	private String lastName;
	
	private String birthdate;
	private Integer age;
}
