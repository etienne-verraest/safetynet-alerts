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
	
	public List<Allergy> findPersonAllergies(Person personEntity) {
		return allergyRepository.findByPerson(personEntity);
		
	}
	
}
