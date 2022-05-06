package com.safetynet.alerts.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDto;
import com.safetynet.alerts.service.AllergyService;
import com.safetynet.alerts.service.MedicationService;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Transactional
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

	@Autowired
	PersonService personService;

	@Autowired
	MedicationService medicationService;

	@Autowired
	AllergyService allergyService;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Creates new allergies or medication for a given person
	 * 
	 * @param medicalRecordDto		The requested body, containing allergies and medications
	 * @return						Created allergies and medications
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@PostMapping
	public ResponseEntity<Person> addMedicalRecord(@RequestBody MedicalRecordDto medicalRecordDto) {

		// Checking if the requestbody is not malformed or not null
		if (medicalRecordDto != null) {
			
			// Fetching informations about our person
			String firstName = medicalRecordDto.getId().getFirstName();
			String lastName = medicalRecordDto.getId().getLastName();
			Person person = personService.getPersonFromDatabase(firstName, lastName);
				
			// Creating a new empty list to add our allergies
			List<Allergy> personAllergies = new ArrayList<Allergy>();
			
			// Mapping allergies : if the allergy already exists, we skip it
			medicalRecordDto.getAllergies().stream()
			.filter(allergyDto -> allergyService.getPersonAllergy(person, allergyDto.getName()) == null)
			.forEach(allergyDto -> { 
					allergyDto.setPerson(person.getId());
					personAllergies.add(modelMapper.map(allergyDto, Allergy.class));
				});
				
			// We won't call the service if there is nothing to update
			if(personAllergies.size() > 0) {
				allergyService.savePersonAllergies(person, personAllergies);
			}

			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}
		
		log.error("[MEDICALRECORD] Request to post medical record is malformed");
		throw new ResourceMalformedException(ExceptionMessages.MEDICALRECORD_MALFORMED_REQUEST);
	}
	
	/**
	 * Get allergies for a given person (first name + last name)
	 * 
	 * @param firstName			String : First name of the person
	 * @param lastName			String : Last name of the person
	 * @return					List<Allergy> : Allergies for the given person
	 * @throws 					ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@GetMapping(path = "/{firstName}/{lastName}/allergies")
	public ResponseEntity<List<Allergy>> getPersonAllergies(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		// Checking if the request is not malformed or not null
		if(firstName != null && lastName != null) {
			
			Person person = personService.getPersonFromDatabase(firstName, lastName);
			
			List<Allergy> allergies = allergyService.getAllPersonAllergies(person);
			return new ResponseEntity<List<Allergy>>(allergies, HttpStatus.FOUND);
		}
	
		log.error("[ALLERGIES] Request to get allergies is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}
	
	// TODO [US] : GET MEDICATIONS

	/**
	 * Delete an allergy for a given person
	 * 
	 * @param firstName			String : First name of the person
	 * @param lastName			String : Last name of the person
	 * @param name				String : Allergy's name
	 * @return					ResponseEntity
	 * @throws 					ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}/allergies/{name}")
	public ResponseEntity<String> deletePersonAllergy(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @PathVariable("name") String name) {

		if (firstName != null && lastName != null && name != null) 
		{
			Person person = personService.getPersonFromDatabase(firstName, lastName);
			
			// Deleting a person allergy by its name
			allergyService.deletePersonAllergy(person, name);
			
			return new ResponseEntity<String>("Deleted Allergy : " + name + " for " + firstName + " " + lastName,
					HttpStatus.OK);
		}
		
		log.error("[ALLERGIES] Request to get allergies is malformed");
		throw new ResourceMalformedException(ExceptionMessages.ALLERGY_MALFORMED_REQUEST);
	}
}
