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
	 * @param address				The address of the fire station
	 * @return						a fire station if it exists
	 */
	public Firestation findFirestationByAddress(String address) {
		return firestationRepository.findByAddress(address);		
	}
	
	/**
	 * Fetch all fire station saved in database
	 * 
	 * @return						a list of available fire stations
	 */
	public List<Firestation> getAllFirestation() {
		log.info("[FIRESTATION] Fetching all firestations");
		return firestationRepository.findAll();
	}
	
	/**
	 * Create a new fire station mapping based on address
	 * 
	 * @param firestation			a "Firestation" object
	 * @return						a "Firestation" object if it doesn't exist
	 */
	public Firestation createFirestation(Firestation firestationEntity) {
		
		if(findFirestationByAddress(firestationEntity.getAddress()) == null) {	
			log.info("[FIRESTATION] Mapping '{}' to firestation number '{}'", firestationEntity.getAddress(),
					firestationEntity.getStationNumber());
			return firestationRepository.save(firestationEntity);
		}
		
		log.error("[FIRESTATION] Could not create firestation with address '{}'", firestationEntity.getAddress());
		throw new ResourceAlreadyExistingException(ExceptionMessages.FIRESTATION_FOUND);
	}
	
	/**
	 * Update a fire station if it exists
	 * 
	 * @param firestation			a "Firestation" object
	 * @return						True if a firestation has been found and updated
	 */
	public Firestation updateFirestation(Firestation firestationEntity) {				
		
		if(findFirestationByAddress(firestationEntity.getAddress()) != null) {			
			log.info("[FIRESTATION] Mapping '{}' to firestation number '{}'", firestationEntity.getAddress(),
					firestationEntity.getStationNumber());
			return firestationRepository.save(firestationEntity);
		}
		
		log.error("[FIRESTATION] Could not update firestation with address '{}'", firestationEntity.getAddress());
		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}

	/**
	 * Delete a fire station in database
	 * 
	 * @param  address				String : The address of the fire station
	 * 
	 */
	public void deleteFirestation(String address) {						
		
		Firestation firestation = findFirestationByAddress(address);				
		
		if(firestation != null) {
			log.info("[FIRESTATION] Deleted fire station with address : '{}'", address);
			firestationRepository.delete(firestation);
			return;
		}	
		
		log.error("[FIRESTATION] Could not delete firestation with address '{}'", address);
		throw new ResourceNotFoundException(ExceptionMessages.FIRESTATION_NOT_FOUND);
	}
	
	/**
	 * Get the firestation number for a given address
	 * 
	 * @param address				String : The address we want to check
	 * @return						Integer : a station number if there is a matching address
	 */
	public Integer getFirestationNumber(String address) {
		log.info("[FIRESTATION] Getting station number for address : {} ", address);
		return firestationRepository.findStationNumberByAddress(address);
	}
	
	/**
	 * Get a list of addresses served by a firestation
	 * 
	 * @param stationNumber			Integer : The desired firestationNumber
	 * @return						List<String> list of matching addresses
	 */
	public List<String> getAddressesFromFirestationNumber(Integer stationNumber) {
		log.info("[FIRESTATION] Getting addresses for station number {} ", stationNumber);
		return firestationRepository.findAddressesByStationNumber(stationNumber);
	}
}
