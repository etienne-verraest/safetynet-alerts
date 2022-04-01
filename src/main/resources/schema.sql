CREATE SCHEMA safetynet;

CREATE TABLE allergy ( 
	id                   INT  NOT NULL    ,
	name                 VARCHAR(100)      ,
	person_name          VARCHAR(100)      
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE firestation ( 
	station_id           INT      ,
	address              VARCHAR(100)      
 ) engine=InnoDB;

CREATE TABLE medication ( 
	id                   INT  NOT NULL    ,
	name                 VARCHAR(100)      ,
	posology             VARCHAR(100)      ,
	person_name          VARCHAR(100)      
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE person ( 
	firstname            VARCHAR(100)  NOT NULL    ,
	lastname             VARCHAR(100)  NOT NULL    ,
	address              VARCHAR(100)      ,
	city                 VARCHAR(100)      ,
	zip                  VARCHAR(100)      ,
	phone                VARCHAR(100)      ,
	email                VARCHAR(100)      ,
	birthdate            VARCHAR(100)      ,
	CONSTRAINT pk_person PRIMARY KEY ( firstname, lastname )
 ) engine=InnoDB;

ALTER TABLE person MODIFY firstname VARCHAR(100)  NOT NULL   COMMENT 'First name of the person, used for primary key';

ALTER TABLE person MODIFY lastname VARCHAR(100)  NOT NULL   COMMENT 'Last name of the person, used for primary key';

