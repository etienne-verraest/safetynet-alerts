package com.safetynet.alerts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import lombok.Data;

@Data
@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;

	public Iterable<Person> getPeopleFromDatabase() {
		return personRepository.findAll();
	}

	public Person createPerson(Person person) {
		return personRepository.save(person);
	}

	// Identificateur unique : combinaison prénom + nom
	public void deletePerson(String firstName, String lastName) {
		//
	}

}
