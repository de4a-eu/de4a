# IDK Mock
IDK entity **mock** based on [API definition](https://app.swaggerhub.com/apis/testdani7/swagger-idk_de_4_a_information_desk/1.0.2#/). Maintains provided information on in-memory DB tables. Mock is able to produce JSON repsonses to the methods:
- /idk/ial/{canonicalEvidenceTypeId}
- /idk/ial/{canonicalEvidenceTypeId}/{countryCode}
- /idk/provision

### Configuration
#### H2 In-Memory database
Initial sql file, contains inserts to set up information published by service
```sh
idk/src/main/resources/import.sql
```
After run application, you should be able to access to H2 Database console to manage SQL data stored. H2 console is published on port defined in `application.properties` either you can define it as a JVM property, for example:
```
-Dh2.console.port.jvm=21082     # default 21080
```
#### Properties
```sh
idk/src/main/resources/application.properties
```
#### Spring config
```sh
idk/src/main/eu/idk/configuration/Conf.java
```

### Model
Data model is based on IDK definition and information provided. Check out entities that conform it on:
```
idk/src/main/java/eu/idk/model
```

### INSTALL
Project is packaged as a WAR to be deployed on an applications server. Run the following command to build it: 
```
mvn clean install
```
WAR output file will be located on the /target folder
##### Constraints
- [de4a-commons library](https://github.com/de4a-wp5/de4a-commons) **development branch** are required, so until package is on maven central, you need to install locally
- Some of common versions with another modules of de4a-connector are on POM parent
- tested on tomcat 9 and Java 11
