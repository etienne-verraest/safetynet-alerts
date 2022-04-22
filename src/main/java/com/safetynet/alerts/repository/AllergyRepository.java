package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;

@Repository
public interface AllergyRepository extends CrudRepository<Allergy, Integer>{

	List<Allergy> findAllByPerson(Person person);
	
	Allergy findByPersonAndName(Person person, String name);
	
	void deleteByPersonAndName(Person person, String name);
	
}
