package com.safetynet.alerts.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloodAlertResponse {

	// List of covered adresses by the stations
	private List<String> addressesServed;
	
	private List<FloodAlertGroupByAddress> personsByAddress;
	
}
