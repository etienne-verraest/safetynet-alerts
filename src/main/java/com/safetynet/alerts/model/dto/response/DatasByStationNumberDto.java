package com.safetynet.alerts.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasByStationNumberDto {

	private List<PersonByStationNumberDto> persons;
	
	private Integer numberOfChildren;
	
	private Integer numberOfAdults;
	
}
