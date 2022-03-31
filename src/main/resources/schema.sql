CREATE SCHEMA safetynet;

CREATE TABLE firestation ( 
	station_id           INT      ,
	address              VARCHAR(100)      
 ) engine=InnoDB;

CREATE TABLE medical_record ( 
	record_id            INT  NOT NULL    PRIMARY KEY,
	birth_date           DATE      ,
	medications          TEXT      ,
	allergies            TEXT      
 ) engine=InnoDB;

CREATE TABLE person ( 
	person_id            INT  NOT NULL  AUTO_INCREMENT  PRIMARY KEY,
	first_name           VARCHAR(100)  NOT NULL    ,
	last_name            VARCHAR(100)  NOT NULL    ,
	address              VARCHAR(255)      ,
	city                 VARCHAR(100)      ,
	zip_code             VARCHAR(100)      ,
	phone_number         VARCHAR(100)      ,
	email                VARCHAR(100)      ,
	medical_record_id    INT  NOT NULL    ,
	CONSTRAINT fk_person_person FOREIGN KEY ( medical_record_id ) REFERENCES medical_record( record_id ) ON DELETE CASCADE ON UPDATE NO ACTION
 ) engine=InnoDB;

