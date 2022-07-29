# Safetynet Alerting System
## Overview
SafetyNet Alerts is a service that wants to save lives by creating alerts for natural phenomenon such as floods or fires.
For a given alert, it queries people that registered to the service. <br>
Hence, emergencies services will be able to locate and get informations about people concerned by such events.

## Configuration
SafetyNet Alerts uses a MySQL database to store the datas. You must change **application.properties** variables accordingly to your configuration <br/>
You must ensure that the database is named as : "**safetynet**". <br>
When your database is created, you can source the schema.sql file to create the database structure.

## Postman collections
Postman collections are in the resources directory of the project for a fast and easy setup of the differents api endpoints.
