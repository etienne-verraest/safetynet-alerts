package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;

@Repository
public interface MedicationRepository extends CrudRepository<Medication, Integer> {
		
	List<Medication> findByPerson(Person person);

}
