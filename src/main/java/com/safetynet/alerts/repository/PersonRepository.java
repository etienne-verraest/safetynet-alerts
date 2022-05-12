package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, PersonId> {

	@Override
	List<Person> findAll();
	
	Person findPersonById(PersonId id);
	
	// CommunityEmail endpoint
	@Query(value = "SELECT email FROM person p WHERE p.city = :requestedCity", nativeQuery= true)
	List<String> findEmailByCity(@Param("requestedCity") String city);

}
