package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j
public class FirestationService {

	@Autowired
	FirestationRepository firestationRepository;
	
	/**
	 * Find a fire station by its address
	 * 
	 * @param address	The address of the fire station
	 * @return			a fire station if it exists
	 */
	public Firestation findFirestationByAddress(String address) {
		return firestationRepository.findByAddress(address);		
	}
	
	/**
	 * Fetch all fire station saved in database
	 * 
	 * @return				a list of available fire stations
	 */
	public List<Firestation> getAllFirestation() {
		log.info("[GET /FIRESTATION] Fetching all firestations");
		return firestationRepository.findAll();
	}
	
	/**
	 * Create a new fire station mapping based on address
	 * 
	 * @param firestation	a "Firestation" object
	 * @return				a "Firestation" object if it doesn't exist
	 */
	public Firestation createFirestation(Firestation firestationEntity) {
		if(findFirestationByAddress(firestationEntity.getAddress()) == null) {	
			log.info("[POST /FIRESTATION] Mapping '{}' to firestation number '{}'", firestationEntity.getAddress(),
					firestationEntity.getStationNumber());
			return firestationRepository.save(firestationEntity);
		}
		log.error("[POST /FIRESTATION] Could not create firestation with address '{}'", firestationEntity.getAddress());
		throw new ResourceAlreadyExistingException(ExceptionMessages.FIRESTATION_FOUND);
	}
	
	/**
	 * Update a fire station if it exists
	 * 
	 * @param firestation	a "Firestation" object
	 * @return				True if a firestation has been found and updated
	 */
	public Firestation updateFirestation(Firestation firestationEntity) {				
		if(findFirestationByAddress(firestationEntity.getAddress()) != null) {			
			log.info("[PUT /FIRESTATION] Mapping '{}' to firestation number '{}'", firestationEntity.getAddress(),
					firestationEntity.getStationNumber());
			return firestationRepository.save(firestationEntity);
		}
		log.error("[PUT /FIRESTATION] Could not update firestation with address '{}'", firestationEntity.getAddress());
		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}

	/**
	 * Delete a fire station in database
	 * 
	 * @param  address	The address of the fire station
	 * @return 
	 */
	public void deleteFirestation(String address) {						
		Firestation firestation = findFirestationByAddress(address);				
		if(firestation != null) {
			log.info("[DELETE /FIRESTATION] Deleted fire station with address : '{}'", address);
			firestationRepository.delete(firestation);
			return;
		}	
		log.error("[DELETE /FIRESTATION] Could not delete firestation with address '{}'", address);
		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}
}
