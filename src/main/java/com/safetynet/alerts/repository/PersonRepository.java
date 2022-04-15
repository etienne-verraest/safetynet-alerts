package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, PersonId> {

	@Override
	public List<Person> findAll();
	
	public Person findPersonById(PersonId id);

}
