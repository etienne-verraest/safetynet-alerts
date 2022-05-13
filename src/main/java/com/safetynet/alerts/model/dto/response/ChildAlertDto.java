package com.safetynet.alerts.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildAlertDto {

	private String address;
	
	// List should contains children (age inferior or equals to 18)
	private List<ChildrenDto> childrens;
	
	// We need to have the adults living with them
	private List<AdultsDto> relatives;
}
