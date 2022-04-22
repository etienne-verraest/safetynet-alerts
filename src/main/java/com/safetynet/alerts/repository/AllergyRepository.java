package com.safetynet.alerts.repository;

import org.springframework.data.repository.CrudRepository;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;

public interface AllergyRepository extends CrudRepository<Allergy, PersonId>{

}
