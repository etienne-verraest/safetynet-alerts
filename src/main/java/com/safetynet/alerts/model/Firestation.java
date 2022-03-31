package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "firestation")
public class Firestation {

	@Id
	@Column(name = "station_id")
	private Integer station;

	@Column(name = "address")
	private String address;

}
