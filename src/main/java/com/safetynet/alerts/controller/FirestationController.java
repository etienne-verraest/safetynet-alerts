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
import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
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
		log.info("[GET /FIRESTATION] Fetching all firestations");
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
		
		String address = firestationDto.getAddress();
		Firestation firestation = firestationService.findFirestationByAddress(address);
		if(firestation == null) {
			
			Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);
			firestation = firestationService.createFirestation(firestationRequestBody);

			log.info("[POST /FIRESTATION] Mapping '{}' to firestation number '{}'", address,
					firestationDto.getStationNumber());
			
			return new ResponseEntity<Firestation>(firestation, HttpStatus.CREATED);
		}	
		
		log.error("[POST /FIRESTATION] Fire station with address '{}' already exists", address);
		throw new ResourceAlreadyExistingException(ExceptionMessages.FIRESTATION_FOUND);
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

		Firestation firestationRequestBody = modelMapper.map(firestationDto, Firestation.class);
		Firestation firestation = firestationService.updateFirestation(firestationRequestBody);

		if (firestation != null) {
			return new ResponseEntity<Firestation>(firestation, HttpStatus.ACCEPTED);
		}

		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}

	/**
	 * This method deletes a fire station given an address
	 * 
	 * @param address The address that has to be deleted
	 * @return A message indicating that the fire station has been deleted
	 * @throws ResourceNotFoundException if the fire station was not found
	 */
	@DeleteMapping(path = "/{address}")
	public ResponseEntity<String> deleteFirestation(@PathVariable("address") String address) {

		// Checking if the fire station exists, if it exists we delete it
		Firestation firestation = firestationService.findFirestationByAddress(address);

		if (firestation != null) {

			firestationService.deleteFirestation(address);

			// Logging the request
			log.info("[DELETE /FIRESTATION] Deleted fire station with address : '{}'", address);
			return new ResponseEntity<String>("Firestation with address '" + address + "' has been deleted",
					HttpStatus.OK);
		}

		// Log an error and throw an exception if the fire station doesn't exist
		log.error("[DELETE /FIRESTATION] Could not delete firestation with address '{}'", address);
		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}

}
