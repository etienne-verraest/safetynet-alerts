package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.safetynet.alerts.model.Firestation;

public interface FirestationRepository extends CrudRepository<Firestation, String> {

	@Override
	public List<Firestation> findAll();
	
	public Firestation findByAddress(String address);
}
