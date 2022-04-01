CREATE SCHEMA safetynet;

CREATE TABLE firestation ( 
	station_id           INT      ,
	address              VARCHAR(100)      
 ) engine=InnoDB;

CREATE TABLE person ( 
	firstname            VARCHAR(100)  NOT NULL    ,
	lastname             VARCHAR(100)  NOT NULL    ,
	address              VARCHAR(100)      ,
	city                 VARCHAR(100)      ,
	zip                  VARCHAR(100)      ,
	phone                VARCHAR(100)      ,
	email                VARCHAR(100)      ,
	birthdate            VARCHAR(100)      ,
	uid                  VARCHAR(200)      ,
	CONSTRAINT pk_person PRIMARY KEY ( firstname, lastname ),
	CONSTRAINT unq_person_firstname UNIQUE ( firstname ) ,
	CONSTRAINT unq_person_lastname UNIQUE ( lastname ) 
 ) engine=InnoDB;

CREATE TABLE allergy ( 
	allergy_id           INT  NOT NULL    ,
	name                 VARCHAR(100)      ,
	p_firstname          VARCHAR(100)      ,
	p_lastname           VARCHAR(100)      
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE medication ( 
	medication_id        INT  NOT NULL    ,
	name                 VARCHAR(100)      ,
	posology             VARCHAR(100)      ,
	p_firstname          VARCHAR(100)  NOT NULL    ,
	p_lastname           VARCHAR(100)  NOT NULL    
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE allergy ADD CONSTRAINT fk_allergy_person FOREIGN KEY ( p_firstname ) REFERENCES person( firstname ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE allergy ADD CONSTRAINT fk_allergy_person_0 FOREIGN KEY ( p_lastname ) REFERENCES person( lastname ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE medication ADD CONSTRAINT fk_medication_person FOREIGN KEY ( p_firstname ) REFERENCES person( firstname ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE medication ADD CONSTRAINT fk_medication_person_0 FOREIGN KEY ( p_lastname ) REFERENCES person( lastname ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE person MODIFY firstname VARCHAR(100)  NOT NULL   COMMENT 'First name of the person, used for primary key';

ALTER TABLE person MODIFY lastname VARCHAR(100)  NOT NULL   COMMENT 'Last name of the person, used for primary key';

