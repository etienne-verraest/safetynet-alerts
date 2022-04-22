package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.AllergyRepository;

@Service
public class AllergyService {

	@Autowired
	AllergyRepository allergyRepository;
	
	public List<Allergy> getAllPersonAllergies(Person personEntity) {
		return allergyRepository.findAllByPerson(personEntity);		
	}
	
	public Allergy getPersonAllergy(Person personEntity, String name) {
		return allergyRepository.findByPersonAndName(personEntity, name);
	}
	
	public void deletePersonAllergy(Person personEntity, String allergyName) {
		allergyRepository.deleteByPersonAndName(personEntity, allergyName);
	}
}
