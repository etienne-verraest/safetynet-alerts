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
import com.safetynet.alerts.model.Medication;
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
	 * @param medicalRecordDto 		The requested body, containing allergies and medications
	 * @return 						Created allergies and medications
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@PostMapping
	public ResponseEntity<Person> addMedicalRecord(@RequestBody MedicalRecordDto medicalRecordDto) {

		if (medicalRecordDto != null) {

			// Creating a new empty list to add our allergies and medications
			List<Allergy> allergies = new ArrayList<>();
			List<Medication> medications = new ArrayList<>();

			// Fetching person
			Person person = personService.getPersonFromDatabase(medicalRecordDto.getId().getFirstName(),
					medicalRecordDto.getId().getLastName());

			// Mapping allergies
			medicalRecordDto.getAllergies().stream()
					.forEach(allergyDto -> allergies.add(modelMapper.map(allergyDto, Allergy.class)));
			allergyService.savePersonAllergies(person, allergies);

			// Mapping medications
			medicalRecordDto.getMedications().stream()
					.forEach(medicationDto -> medications.add(modelMapper.map(medicationDto, Medication.class)));
			medicationService.savePersonMedications(person, medications);

			return new ResponseEntity<>(person, HttpStatus.ACCEPTED);
		}

		log.error("[MEDICALRECORD] Request to post medical record is malformed");
		throw new ResourceMalformedException(ExceptionMessages.MEDICALRECORD_MALFORMED_REQUEST);
	}

	/**
	 * Get allergies for a given person (first name + last name)
	 * 
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @return 						List<Allergy> : Allergies for the given person
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@GetMapping(path = "/{firstName}/{lastName}/allergies")
	public ResponseEntity<List<Allergy>> getPersonAllergies(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		if (firstName != null && lastName != null) {
			Person person = personService.getPersonFromDatabase(firstName, lastName);

			List<Allergy> allergies = allergyService.getAllPersonAllergies(person);
			return new ResponseEntity<>(allergies, HttpStatus.FOUND);
		}

		log.error("[ALLERGIES] Request to get allergies is malformed");
		throw new ResourceMalformedException(ExceptionMessages.ALLERGY_MALFORMED_REQUEST);
	}

	/**
	 * Get medications for a given person (first name + last name)
	 * 
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @return 						List<Medication> : Medications for the given person
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@GetMapping(path = "/{firstName}/{lastName}/medications")
	public ResponseEntity<List<Medication>> getPersonMedications(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		if (firstName != null && lastName != null) {
			Person person = personService.getPersonFromDatabase(firstName, lastName);

			List<Medication> medications = medicationService.getAllPersonMedications(person);
			return new ResponseEntity<>(medications, HttpStatus.FOUND);
		}

		log.error("[MEDICATIONS] Request to get medication is malformed");
		throw new ResourceMalformedException(ExceptionMessages.MEDICATION_MALFORMED_REQUEST);
	}

	/**
	 * Delete an allergy for a given person
	 * 
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @param name      			String : Allergy's name
	 * @return ResponseEntity
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}/allergies/{name}")
	public ResponseEntity<String> deletePersonAllergy(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @PathVariable("name") String name) {

		if (firstName != null && lastName != null && name != null) {
			Person person = personService.getPersonFromDatabase(firstName, lastName);

			allergyService.deletePersonAllergy(person, name);
			return new ResponseEntity<>("Deleted Allergy : " + name + " for " + firstName + " " + lastName,
					HttpStatus.OK);
		}

		log.error("[ALLERGIES] Request to delete allergies is malformed");
		throw new ResourceMalformedException(ExceptionMessages.ALLERGY_MALFORMED_REQUEST);
	}
	
	/**
	 * Delete a medication for a given person
	 * 
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @param name      			String : Medication's namePosology
	 * @return ResponseEntity
	 * @throws 						ResourceMalformedException : an error is thrown if the request is incorrect
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}/medications/{namePosology}")
	public ResponseEntity<String> deletePersonMedication(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @PathVariable("namePosology") String namePosology) {

		if (firstName != null && lastName != null && namePosology != null) {
			Person person = personService.getPersonFromDatabase(firstName, lastName);

			medicationService.deletePersonMedication(person, namePosology);
			return new ResponseEntity<>("Deleted medication : " + namePosology + " for " + firstName + " " + lastName,
					HttpStatus.OK);
		}

		log.error("[MEDICATIONS] Request to delete medication is malformed");
		throw new ResourceMalformedException(ExceptionMessages.MEDICATION_MALFORMED_REQUEST);
	}
}
