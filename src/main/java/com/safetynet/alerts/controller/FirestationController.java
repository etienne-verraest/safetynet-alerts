package com.safetynet.alerts.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.dto.FirestationDto;
import com.safetynet.alerts.service.FirestationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/firestation")
public class FirestationController {

	@Autowired
	private FirestationService firestationService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * This method returns a list of every fire station
	 * 
	 * @return List<Firestation> List of fire stations
	 */
	@GetMapping
	public List<Firestation> returnFirestations() {
		return firestationService.getAllFirestation();
	}

	/**
	 * This method creates a {@link Firestation.java}, using address as a primary
	 * key
	 * 
	 * @param firestationDto (@link FirestationDto.java}
	 * @return a new {@link Firestation.java} entity
	 */
	@PostMapping
	public ResponseEntity<Firestation> createFirestation(@RequestBody FirestationDto firestationDto) {

		// Checking if the RequestBody is not null
		if (firestationDto != null) {

			// Mapping DTO to Entity
			Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);

			// Returning the result
			Firestation firestation = firestationService.createFirestation(firestationRequestBody);
			return new ResponseEntity<Firestation>(firestation, HttpStatus.CREATED);
		}

		// Logging the error if firestationDto is malformed
		log.error("[POST /FIRESTATION] Request body of the firestation is malformed");
		throw new IllegalArgumentException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	/**
	 * This method updates a Fire station. The fire station must be registered in
	 * the database
	 * 
	 * @param firestationDto {@link FirestationDto.java}
	 * @return an updated {@link Firestation.java} entity
	 * @throws ResourceNotFoundException if the fire station was not found
	 */
	@PutMapping
	public ResponseEntity<Firestation> updateFirestation(@RequestBody FirestationDto firestationDto) {

		// Checking if the request body is not null
		if (firestationDto != null) {

			// Mapping DTO to Entity
			Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);

			// Returning the result
			Firestation firestation = firestationService.updateFirestation(firestationRequestBody);
			return new ResponseEntity<Firestation>(firestation, HttpStatus.ACCEPTED);
		}

		// Logging the error if firestationDto is malformed
		log.error("[PUT /FIRESTATION] Request to update firestation is malformed");
		throw new IllegalArgumentException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	/**
	 * This method deletes a fire station given an address
	 * 
	 * @param address The address that has to be deleted
	 * @return
	 * @throws
	 */
	@DeleteMapping(path = "/{address}")
	public ResponseEntity<String> deleteFirestation(@PathVariable("address") String address) {

		// Checking if the address is non null
		if (address != null) {

			// Returning the result
			firestationService.deleteFirestation(address);
			return new ResponseEntity<String>("Firestation with address '" + address + "' has been deleted",
					HttpStatus.OK);
		}

		// Logging the error if the address is null
		log.error("[DELETE /FIRESTATION] Request to delete firestation is malformed");
		throw new IllegalArgumentException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}
}
