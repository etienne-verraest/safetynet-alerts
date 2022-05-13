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
	
	// Find person by its identifier
	Person findPersonById(PersonId id);
	
	// CommunityEmail endpoint
	@Query(value = "SELECT email FROM person p WHERE p.city = :requestedCity", nativeQuery= true)
	List<String> findEmailByCity(@Param("requestedCity") String city);
	
	// ChildAlert
	// @Query(value = "SELECT floor(DATEDIFF(curdate() , STR_TO_DATE(birthdate, '%d/%m/%Y'))/365) AS age FROM person p WHERE p.address LIKE :requestedAddress HAVING age <= 18", nativeQuery = true)
	// List<Integer> checkIfChildrenArePresentAtAddress(@Param("requestedAddress") String address);

	// Find every people living at given address
	List<Person> findAllByAddress(String address);
	
}
