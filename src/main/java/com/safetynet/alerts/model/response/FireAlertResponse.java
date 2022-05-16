package com.safetynet.alerts.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireAlertResponse {

	private String address;
	private Integer stationNumber;
	
	private List<PersonFireAlertResponse> persons;
	
}
