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
@Table()
@AllArgsConstructor
@NoArgsConstructor
public class Firestation {

	@Id
	@Column
	private String address;

	@Column(name = "station_number")
	private Integer stationNumber;
}
