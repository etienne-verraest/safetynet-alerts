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
	email                VARCHAR(100)      
 ) engine=InnoDB;

CREATE TABLE medical_record ( 
	person_id            INT  NOT NULL    ,
	birth_date           DATE      ,
	medications          TEXT      ,
	allergies            TEXT      
 ) engine=InnoDB;

ALTER TABLE medical_record ADD CONSTRAINT fk_person_id FOREIGN KEY ( person_id ) REFERENCES person( id ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE person COMMENT 'This table contains information about a person (first name, last name, adress, city, zip code, phone number and email)';

