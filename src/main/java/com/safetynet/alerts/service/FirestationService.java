package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;

import lombok.Data;

@Data
@Service
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
		return firestationRepository.findAll();
	}
	
	/**
	 * Create a new fire station mapping based on address
	 * 
	 * @param firestation	a "Firestation" object
	 * @return				a "Firestation" object if it doesn't exist
	 */
	public Firestation createFirestation(Firestation firestationEntity) {
		return firestationRepository.save(firestationEntity);
	}
	
	/**
	 * Update a fire station if it exists
	 * 
	 * @param firestation	a "Firestation" object
	 * @return				True if a firestation has been found and updated
	 */
	public Firestation updateFirestation(Firestation firestationEntity) {
		Firestation firestation = findFirestationByAddress(firestationEntity.getAddress());				
		if(firestation != null) {
			return firestationRepository.save(firestationEntity);
		}
		return null;
	}

	/**
	 * Delete a fire station in database
	 * 
	 * @param  address	The address of the fire station
	 * @return boolean	True if the fire station has been deleted
	 */
	public void deleteFirestation(String address) {
		Firestation firestationEntity = findFirestationByAddress(address);		
		if(firestationEntity != null) {
			firestationRepository.delete(firestationEntity);
		}
	}
}
