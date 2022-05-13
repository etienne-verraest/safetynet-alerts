package com.safetynet.alerts.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildAlertResponse {

	private String address;
	
	// List should contains children (age inferior or equals to 18)
	private List<ChildrenResponse> childrens;
	
	// We need to have the adults living with them
	private List<AdultsResponse> relatives;
}
