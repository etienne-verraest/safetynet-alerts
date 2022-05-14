package com.safetynet.alerts.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloodAlertGroupByAddress {

	private String address;
	
	private List<PersonFloodAlertResponse> persons;
}
