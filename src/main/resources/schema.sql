CREATE SCHEMA safetynet;

CREATE TABLE firestation ( 
	station_id           INT      ,
	address              VARCHAR(100)      
 ) engine=InnoDB;

CREATE TABLE person ( 
	id                   INT  NOT NULL  AUTO_INCREMENT  PRIMARY KEY,
	first_name           VARCHAR(100)  NOT NULL    ,
	last_name            VARCHAR(100)  NOT NULL    ,
	address              VARCHAR(255)      ,
	city                 VARCHAR(100)      ,
	zip_code             INT      ,
	phone_number         VARCHAR(100)      ,
	email                VARCHAR(100)      ,
	medical_record_id    INT  NOT NULL    ,
	CONSTRAINT unq_person_medical_record_id UNIQUE ( medical_record_id ) 
 ) engine=InnoDB;

CREATE TABLE medical_record ( 
	id                   INT  NOT NULL    PRIMARY KEY,
	birth_date           DATE      ,
	medications          TEXT      ,
	allergies            TEXT      ,
	CONSTRAINT fk_medical_record_person FOREIGN KEY ( id ) REFERENCES person( medical_record_id ) ON DELETE NO ACTION ON UPDATE NO ACTION
 ) engine=InnoDB;

ALTER TABLE person COMMENT 'This table contains information about a person (first name, last name, adress, city, zip code, phone number and email)';

