package com.safetynet.alerts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.response.ChildAlertResponse;
import com.safetynet.alerts.model.response.FireAlertResponse;
import com.safetynet.alerts.model.response.FirestationResponse;
import com.safetynet.alerts.service.AlertsService;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AlertsController {

	@Autowired
	private PersonService personService;

	@Autowired
	private AlertsService alertsService;

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

			// Get Phone numbers
			List<String> phoneNumbers = alertsService.getPhoneAlert(firestationNumber);

			log.info("[GET /PHONEALERT] {} phones numbers have been found", phoneNumbers.size());
			return new ResponseEntity<List<String>>(phoneNumbers, HttpStatus.FOUND);
		}

		log.error("[GET /PHONEALERT] Request to get phone numbers is malformed");
		throw new ResourceMalformedException("Request to get phone numbers is malformed");
	}

	/**
	 * Get a list of person concerned by a fire alert
	 * 
	 * @param address					String : the address concerned by the fire alert
	 * @return							List<PersonsInFireAlertDto> Persons concerned by the fire Alert
	 */
	@GetMapping(path = "/fire")
	public List<FireAlertResponse> fireAlert(@RequestParam String address) {

		if (address != null) {
			log.info("[GET /FIRE] Checking for persons living at {}", address);
			return alertsService.getFireAlert(address);
		}

		log.error("[GET /FIRE] Request to get persons at the given address is malformed");
		throw new ResourceMalformedException("Request to get persons at the given address is malformed");
	}

	/**
	 * Get a list of children for a given address
	 * 
	 * @param address					String : the address we want to check
	 * @return							ChildAlertDto: List of children and their relatives
	 */
	@GetMapping(path = "/childAlert")
	public ChildAlertResponse childAlert(@RequestParam String address) {

		if (address != null) {
			log.info("[GET /CHILDALERT] Checking for children at address {}", address);
			return alertsService.getChildAlert(address);
		}

		log.error("[GET /CHILDALERT] Request to get children at the given address is malformed");
		throw new ResourceMalformedException("Request to get children at the given address is malformed");
	}

	/**
	 * Get a list of person given a firestation number
	 * 
	 * @param stationNumber				Integer : the firestationNumber
	 * @return							DatasByStationNumberDto : All requested datas for a station number
	 */
	@GetMapping(path = "/firestation")
	public FirestationResponse getPersonsByStationNumber(@RequestParam Integer stationNumber) {

		if (stationNumber != null && stationNumber >= 0) {	
			return alertsService.getFirestationAlert(stationNumber);
		}

		log.error("[GET /FIRESTATION] Request to get persons at the given firestation number is malformed");
		throw new ResourceMalformedException("Request to get persons at the given firestation number is malformed");
	}

}
