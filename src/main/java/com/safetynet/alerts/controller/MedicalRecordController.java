package com.safetynet.alerts.controller;

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
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDto;
import com.safetynet.alerts.model.dto.MedicationDto;
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
	 * This method returns medications for a given person
	 * 
	 * @param firstName The first name of the person
	 * @param lastName  The last name of the person
	 * @return A list of medications
	 * @throws ResourceNotFoundException if the person was not found in database
	 */
	@GetMapping(path = "/{firstName}/{lastName}/medications")
	public ResponseEntity<List<Medication>> getPersonMedications(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person != null) {
			List<Medication> medications = medicationService.findPersonMedications(person);
			return new ResponseEntity<List<Medication>>(medications, HttpStatus.FOUND);
		}
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * This method returns allergies for a given person
	 * 
	 * @param firstName The first name of the person
	 * @param lastName  The last name of the person
	 * 
	 * @return A list of allergies
	 * 
	 * @throws ResourceNotFoundException if the person was not found in database
	 */
	@GetMapping(path = "/{firstName}/{lastName}/allergies")
	public ResponseEntity<List<Allergy>> getPersonAllergies(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person != null) {
			List<Allergy> allergies = allergyService.getAllPersonAllergies(person);
			return new ResponseEntity<List<Allergy>>(allergies, HttpStatus.FOUND);
		}
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * This method deletes an allergy for a given person
	 * 
	 * @param firstName The first name of the person
	 * @param lastName  The last name of the person
	 * 
	 * @return
	 * 
	 * @throws ResourceNotFoundException if the person was not found in database
	 * @throws ResourceNotFoundException if the allergy for the given person was not found
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}/allergies/{name}")
	public ResponseEntity<String> deletePersonAllergy(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @PathVariable("name") String name) {

		// Checking if the person exists
		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person != null) {

			// Checking if the person has the specified allergy
			if (allergyService.getPersonAllergy(person, name) != null) {

				// Deleting allergy if found and updating person
				allergyService.deletePersonAllergy(person, name);
				personService.updatePerson(person);

				// Logging the request
				log.info("[DELETE /MEDICALRECORD] Deleted allergy '{}' for {} {}", name, firstName, lastName);
				
				return new ResponseEntity<String>("Delete Allergy : " + name + " for " + firstName + " " + lastName,
						HttpStatus.OK);
			}
			// Logging the error
			log.error("[DELETE /MEDICALRECORD] Allergy '{}' was not found for {} {}", name, firstName, lastName);
			throw new ResourceNotFoundException(ExceptionMessages.ALLERGY_NOT_FOUND);
		}
		// Logging the error
		log.error("[DELETE /MEDICALRECORD] Person with name '{} {}' was not found in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * This method adds a new medical record to a person
	 * 
	 * @param firstName     The first name of the person
	 * @param lastName      The last name of the person
	 * @param medicationDto {@link MedicationDto.java}
	 * @return updated information about a person
	 * @throws ResourceNotFoundException if the person was not found in database
	 */
	@PostMapping
	public ResponseEntity<Person> addMedicalRecord(@RequestBody MedicalRecordDto medicalRecordDto) {

		String firstName = medicalRecordDto.getId().getFirstName();
		String lastName = medicalRecordDto.getId().getLastName();

		// Checking if the person exists in database
		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person != null) {

			PersonId personId = person.getId();

			List<Allergy> personAllergies = person.getAllergies();
			List<Medication> personMedications = person.getMedications();

			// Mapping Allergy Dto to Person's entity
			medicalRecordDto.getAllergies().forEach(allergyDto -> {
				allergyDto.setPerson(personId);
				personAllergies.add(modelMapper.map(allergyDto, Allergy.class));
			});

			// Mapping Medication Dto to Person's entity
			medicalRecordDto.getMedications().forEach(medicationDto -> {
				medicationDto.setPerson(personId);
				personMedications.add(modelMapper.map(medicationDto, Medication.class));
			});

			// Adding allergies and medications to Person entity
			person.setAllergies(personAllergies);
			person.setMedications(personMedications);

			// Updating person's entity
			personService.updatePerson(person);

			// Logging our request
			log.info("[POST /MEDICALRECORD] Added Medical record for {} {}", firstName, lastName);

			// Returning person entity with medicalRecord information updated
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}

		// Logging the error
		log.error("[POST /MEDICALRECORD] Person with name '{} {}' was not found in database", firstName, lastName);

		// Throwing an exception if the person doesn't exist
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
}
