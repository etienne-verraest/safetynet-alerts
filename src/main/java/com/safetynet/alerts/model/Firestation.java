package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "firestation")
@AllArgsConstructor
@NoArgsConstructor
public class Firestation {

	@Id
	@Column
	private String address;
	
	@Column
	private Integer stationNumber;
}
