package com.safetynet.alerts.model.dto.response;

import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildrenDto {

	private PersonId id;	
	private String birthdate;
	private Integer age;
}
