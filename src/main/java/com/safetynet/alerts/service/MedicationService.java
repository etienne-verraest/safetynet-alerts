package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicationRepository;

@Service
public class MedicationService {

	@Autowired
	MedicationRepository medicationRepository;
	
	public List<Medication> findPersonMedication(Person personEntity) {
		return medicationRepository.findByPerson(personEntity);
	}
	
}
