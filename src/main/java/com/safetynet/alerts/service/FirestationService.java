package com.safetynet.alerts.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
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
	 * Checks if a firestation exists
	 * 
	 * @param address				String : the address of the fire station
	 * @return						True if a firestation is found at given address
	 */
	public boolean checkIfFirestationExists(String address) {
		
		if(firestationRepository.findByAddress(address) != null) {
			return true;
		}
		return false;
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
		
		if(!checkIfFirestationExists(firestationEntity.getAddress())) {
			log.info("[FIRESTATION] Mapping '{}' to firestation number '{}'", firestationEntity.getAddress(),
					firestationEntity.getStationNumber());
			return firestationRepository.save(firestationEntity);
		}
		
		return null;
	}
	
	/**
	 * Create new fire stations mapping from a list
	 * 
	 * @param listOfFirestation		a List of firestation
	 * @return						A List of saved firestation
	 */
	public Iterable<Firestation> createFirestationFromList(List<Firestation> listOfFirestation) {
		
		if(!listOfFirestation.isEmpty()) {
			List<Firestation> firestations = listOfFirestation.stream()
			.filter(f -> !checkIfFirestationExists(f.getAddress()))
			.collect(Collectors.toList());
			
			firestations.forEach(f -> log.info("[FIRESTATION] Mapping '{}' to firestation number '{}'", f.getAddress(), f.getStationNumber()));
			return firestationRepository.saveAll(firestations);
		}
		return Collections.emptyList();
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
	 * @param stationNumber			Integer : The desired firestation Number
	 * @return						List<String> list containing matching addresses
	 */
	public List<String> getAddressesFromFirestationNumber(Integer stationNumber) {	
		
		List<String> response = firestationRepository.findAddressesByStationNumber(stationNumber);
		
		if(!response.isEmpty()) {
			log.info("[FIRESTATION] Getting addresses for station number {} ", stationNumber);
			return response;
		}
		
		throw new ResourceNotFoundException("There are no addresses linked to firestation number " + stationNumber);
	}
}
