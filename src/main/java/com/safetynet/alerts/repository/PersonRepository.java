package com.safetynet.alerts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.mapper.PersonId;

@Repository
public interface PersonRepository extends CrudRepository<Person, PersonId> {

}
