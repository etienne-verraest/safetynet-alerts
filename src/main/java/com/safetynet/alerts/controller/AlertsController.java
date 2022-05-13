package com.safetynet.alerts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonsInFireAlertDto;
import com.safetynet.alerts.service.AllergyService;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.MedicationService;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AlertsController {

	@Autowired
	private PersonService personService;

	@Autowired
	private FirestationService firestationService;

	@Autowired
	private MedicationService medicationService;

	@Autowired
	private AllergyService allergyService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Get mail addresses for a given city
	 * 
	 * @param city						String : The name of the city
	 * @return							List<String> of corresponding mail addresses
	 */
	@GetMapping(path = "/communityEmail")
	public ResponseEntity<List<String>> getEmails(@RequestParam String city) {

		if (!city.isBlank()) {

			List<String> response = personService.getEmailsByCity(city);
			return new ResponseEntity<List<String>>(response, HttpStatus.FOUND);
		}

		log.error("[GET /COMMUNITYEMAIL] Request to get emails by city is malformed");
		throw new ResourceMalformedException("Request to get emails is malformed");
	}

	/**
	 * Get phone numbers for a given firestation number
	 * 
	 * @param firestationNumber			Integer : the number of the firestation
	 * @return							List<String> of corresponding phone numbers
	 */
	@GetMapping(path = "/phoneAlert")
	public ResponseEntity<List<String>> phoneAlert(@RequestParam Integer firestationNumber) {

		if (firestationNumber != null && firestationNumber > 0) {

			// Get persons served by the firestation number
			List<Person> persons = personService
					.findPersonByAddresses(firestationService.getAddressesFromFirestationNumber(firestationNumber));

			// Get phone numbers of served persons
			List<String> phoneNumbers = persons.stream().map(p -> p.getPhone()).collect(Collectors.toList());

			log.info("[GET /PHONEALERT] {} numbers phones have been found", phoneNumbers.size());
			return new ResponseEntity<List<String>>(phoneNumbers, HttpStatus.FOUND);
		}

		log.error("[GET /PHONEALERT] Request to get phone numbers is malformed");
		throw new ResourceMalformedException("Request to get phone numbers is malformed");
	}
	
	/**
	 * Get a list of person concerned by a fire alert
	 * 
	 * @param address				String : the address concerned by the fire alert
	 */
	@GetMapping(path = "/fire")
	public List<PersonsInFireAlertDto> fireAlert(@RequestParam String address) {
		
		if(address != null) {
			
			List<PersonsInFireAlertDto> personsConcernedByFireAlert = personService.getPersonsConcernedByFireAlert(address);
			Integer firestationNumber = firestationService.getFirestationNumber(address);
			
			personsConcernedByFireAlert.forEach(person -> person.setStationNumber(firestationNumber));
			return personsConcernedByFireAlert;
		}
		
		log.error("[GET /FIRE] Request to get persons at the given address is malformed");
		throw new ResourceMalformedException("Request to get persons at the given address is malformed");
	}

}
