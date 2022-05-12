package com.safetynet.alerts.controller;

import java.util.ArrayList;
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
	 * @param city					String : The name of the city
	 * @return						List<String> of corresponding mail addresses
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
	 * @param firestationNumber		Integer : the number of the firestation
	 * @return						List<String> of corresponding phone numbers
	 */
	@GetMapping(path = "/phoneAlert")
	public ResponseEntity<List<String>> phoneAlert(@RequestParam Integer firestationNumber) {

		if (firestationNumber != null && firestationNumber > 0) {

			List<Person> persons = new ArrayList<Person>();
			
			// Get persons served by firestation number
			firestationService.getAddressesFromFirestationNumber(firestationNumber).stream()
					.forEach(address -> persons.addAll(personService.findPersonByAddress(address)));
			
			// Get phone numbers of served persons
			List<String> phoneNumbers = persons.stream().map(p -> p.getPhone()).collect(Collectors.toList());
			
			return new ResponseEntity<List<String>>(phoneNumbers, HttpStatus.FOUND);
		}
		
		log.error("[GET /PHONEALERT] Request to get phone numbers is malformed");
		throw new ResourceMalformedException("Request to get phone numbers is malformed");
	}

}
