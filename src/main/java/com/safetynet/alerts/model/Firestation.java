package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "firestation")
public class Firestation {

	@Column(name = "station_id")
	private Integer id;

	@Column(name = "address")
	private String address;

}
