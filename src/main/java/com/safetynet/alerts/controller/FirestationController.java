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

	// TODO [CLEAN CODE] : Rewrite full java documentation on controllers and services
	
	@Autowired
	private FirestationService firestationService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping
	public List<Firestation> returnFirestations() {
		return firestationService.getAllFirestation();
	}

	@PostMapping(path = "/firestation")
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
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	@PutMapping(path = "/firestation")
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
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}

	@DeleteMapping(path = "/firestation/{address}")
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
		throw new ResourceMalformedException(ExceptionMessages.FIRESTATION_MALFORMED_REQUEST);
	}
}
