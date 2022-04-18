package com.safetynet.alerts.model.dto;

import java.util.List;

import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDto {

	private PersonId id;
	
	private List<MedicationDto> medications;
	
	private List<AllergyDto> allergies;
		
}
