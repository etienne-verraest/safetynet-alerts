package com.safetynet.alerts.mapper;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonId implements Serializable {

	private String firstName;

	private String lastName;

}
