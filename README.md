# DE4A - CONNECTOR PROJECT
Repository revolves around [de4a-connector](https://github.com/de4a-wp5/de4a/tree/developer/de4a-connector) component, but in order to test, enhance and complete the functionality, an complete environment is created. We will describes the differents packages.

## de4a-commons
Concived as a library that maintains:
- Utils and general purpose methods
- Schemes and JAXB objects (`this will be replaced by` [de4a-commons repository](https://github.com/de4a-wp5/de4a-commons))
- Exceptions objects
- Common data model and repositories

## de4a-evaluator
WebService as a very basic functionality of an Evaluator entity. Regarding basics task that perform:
- Build requests
- Preview responses
- Interaction with requestor

## de4a-idk
IDK entity mock based on [API definition](https://app.swaggerhub.com/apis/testdani7/swagger-idk_de_4_a_information_desk/1.0.2#/). Maintains provided information on in-memory DB tables. Mock is able to provide repsonse to the interfaces:
- /idk/{canonicalEvidenceTypeId}/{countryCode}
- /idk/ial/{atuCode}

### Configuration
#### H2 In-Memory database
Inserts sql file to set up information provided by service
```sh
idk/src/main/resources/import.sql
```
#### Properties
```sh
idk/src/main/resources/application.properties
```
#### Spring config
```sh
idk/src/main/eu/idk/configuration/Conf.java
```

## de4a-pid
Library which goal would be support evidence processing to connector as a gateway towards the Owner to implements differents pre/post processings of evidences. It came up as temporary solution to certains problematicals. `Rigth now it is not integrated`

## Smp
SMP mock to provide service metadata. `deprecated`

## de4a-connector
Checkout technical documentation [DE4A Connector - Installation and configuration v1.0.docx] (`pending link to owncloud`)
### API doc
Once you deploy a Connector instace it be able to access to Swagger UI browsing:
```sh
http://endpoint:port/swagger-ui/
```
Even so, API definition is published on:
[Public Swagger API Connector](https://app.swaggerhub.com/apis/de4a/Connector/0.1.0)

## INSTALL
You should be able to compile hole packages from parent POM file:
```sh
mvn clean install -Dmaven.test.skip=true
```
Also it is possible to compile each package separately browsing into the folder and running the command above

**Until [de4a-commons](https://github.com/de4a-wp5/de4a-commons) is on maven central) install the de4a-commons project locally.**
