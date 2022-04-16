package com.safetynet.alerts.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.AllergyDto;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Transactional
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

	@Autowired
	PersonService personService;

	/**
	 * This method adds a new allergy to a person medical record 
	 * 
	 * @param firstName		The first name of the person
	 * @param lastName		The last name of the person
	 * @param allergyDto	{@link AllergyDto.java}
	 * @return updated information about a person
	 * @throws ResourceNotFoundException if the person was not found in database
	 */
	@PostMapping("/{firstName}/{lastName}/allergy")
	public ResponseEntity<Person> addAllergy(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @RequestBody AllergyDto allergyDto) {

		// Checking if the person exists in database
		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person != null) {

			// Getting person's allergy
			List<Allergy> personAllergies = person.getAllergies();

			// It is a POST request, it means that we are adding a new allergy
			Allergy allergy = new Allergy();
			allergy.setName(allergyDto.getName());
			allergy.setPerson(person);
			
			personAllergies.add(allergy);
			person.setAllergies(personAllergies);

			// Updating person's information
			personService.updatePerson(person);
			
			// Logging the request
			log.info("[POST /MEDICALRECORD] Adding allergy '{}' to {} {}'s medical record", allergyDto.getName(), firstName, lastName);

			// Returning person entity with medicalRecord information updated
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}
		
		// Logging the error
		log.error("[PUT /FIRESTATION] Person with name '{} {}' was not found in database",
				firstName, lastName);
		
		// Throwing an exception if the person doesn't exist
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
}
