package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.safetynet.alerts.model.Firestation;

public interface FirestationRepository extends CrudRepository<Firestation, String> {

	@Override
	List<Firestation> findAll();
	
	Firestation findByAddress(String address);
	
	@Query(value = "SELECT stationNumber from firestation f where address = :requestedAddress", nativeQuery = true)
	Integer findStationNumberByAddress(@Param("requestedAddress") String address);
	
	@Query(value = "SELECT address from firestation f where stationNumber = :requestedNumber", nativeQuery = true)
	List<String> findAddressesByStationNumber(@Param("requestedNumber") Integer stationNumber);

}
