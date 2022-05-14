package com.safetynet.alerts.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.dto.FirestationDto;
import com.safetynet.alerts.service.FirestationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FirestationController {
	
	@Autowired
	private FirestationService firestationService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Get a list of every firestation
	 * 
	 * @return						A List<Firestation> containing existing firestations
	 */
	@GetMapping(path = "/firestations")
	public List<Firestation> returnFirestations() {
		return firestationService.getAllFirestation();
	}
	
	/**
	 * Create a firestation
	 * 
	 * @param firestationDto		A FirestationDTO that contains informations about firestation	
	 * @return						The newly created firestation
	 */
	@PostMapping(path = "/firestation")
	public ResponseEntity<Firestation> createFirestation(@RequestBody FirestationDto firestationDto) {

		if (firestationDto != null) {
			Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);

			Firestation firestation = firestationService.createFirestation(firestationRequestBody);
			return new ResponseEntity<>(firestation, HttpStatus.CREATED);
		}

		log.error("[FIRESTATION] Request body of the firestation is malformed");
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	/**
	 * Update a firestation
	 * 
	 * @param firestationDto		A FirestationDTO that contains informations about firestation
	 * @return						The updated firestation
	 */
	@PutMapping(path = "/firestation")
	public ResponseEntity<Firestation> updateFirestation(@RequestBody FirestationDto firestationDto) {

		if (firestationDto != null) {
			Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);

			Firestation firestation = firestationService.updateFirestation(firestationRequestBody);
			return new ResponseEntity<>(firestation, HttpStatus.ACCEPTED);
		}

		log.error("[FIRESTATION] Request to update firestation is malformed");
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	/**
	 * Delete a firestation by its address
	 * 
	 * @param address				String : the address of the firestation
	 * @return						A 201 HTTP CODE, meaning the firestation has been deleted
	 */
	@DeleteMapping(path = "/firestation")
	public ResponseEntity<String> deleteFirestation(@RequestParam String address) {

		if (address != null) {
			firestationService.deleteFirestation(address);
			return new ResponseEntity<>("Firestation with address '" + address + "' has been deleted",
					HttpStatus.OK);
		}

		log.error("[FIRESTATION] Request to delete firestation is malformed");
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}
}
