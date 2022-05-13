package com.safetynet.alerts.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireAlertResponse {

	@JsonIgnore
	private PersonId id;
	
	private String firstName;
	private String lastName;
	
	private String address;
	private Integer stationNumber;
	
	private String phone;
	
	private String birthdate;
	private Integer age;
	
	@JsonInclude(Include.NON_EMPTY)
	private List<Medication> medications;
	
	@JsonInclude(Include.NON_EMPTY)
	private List<Allergy> allergies;
}
