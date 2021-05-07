
# DE4A - CONNECTOR
Connector component. Check out following instructions and descriptions.

## de4a-commons
Conceived as a library that maintains:
- Utils and general purpose methods
- Common classes

## de4a-idk
Mocked IDK entity based on [API definition](https://app.swaggerhub.com/apis/danieldecastrop/swagger-idk_de_4_a_information_desk/2.0.1#/). It maintains provided information in the in-memory DB tables. Mock is able to process requests through the interfaces:
- /idk/ial/{canonicalEvidenceTypeId}
- /idk/ial/{canonicalEvidenceTypeId}/{countryCode}
- /idk/provision

### Configuration
#### H2 In-Memory database
SQL file with the inserts to set up the information provided by the service
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

## de4a-connector
Checkout the technical documentation on [DE4A Connector - Installation and configuration](https://newrepository.atosresearch.eu/index.php/f/1059081)
### API doc
Once you deploy a Connector instance, it will be able to access to Swagger UI browsing:
```sh
http://connector-endpoint:port/swagger-ui/
```
Even so, the API definition is published at:
[Public Swagger API Connector](https://app.swaggerhub.com/apis/de4a/Connector/0.1.0)



### Configuration
#### Application properties
Global application properties definitions:
```sh
~/de4a/de4a-connector/src/main/resources/application.properties
```
#### Phase4 Properties
TOOP and AS4 configuration properties
```sh
~/de4a/de4a-connector/src/main/resources/phase4.properties
```
#### Spring config
```sh
~/de4a/de4a-connector/src/main/java/eu/de4a/connector/service/spring/Conf.java
```
## Installation
You should be able to compile entire packages from the parent POM file:
```sh
mvn clean install
```
It is also possible to compile each package separately by browsing to the folder and running the command above.
#### Package
The compilation process will packaging the project into a `.war` file located on `/target/` path, which should be deployable on any applications server. If you compile the parent pom, the IDK and Connector target paths will be created with their corresponding `war` files.

#### de4a-commons `v0.1.3`
[de4a-commons](https://github.com/de4a-wp5/de4a-commons) project is now on maven central [OSS Sonatype repository](https://search.maven.org/search?q=g:eu.de4a)

#### Toop version `v2.1.2-SNAPSHOT`
Due to the last changes on [de4a-commons](https://github.com/de4a-wp5/de4a-commons/tree/development) Toop-connector-ng version should be `2.1.2-SNAPSHOT`, so you may need to add following repo server on your maven settings
```sh
https://oss.sonatype.org/content/repositories/snapshots/
```
## Connector configuration guide
For a correct configuration of the Connector, three main property files must be cosidered:
- `application.properties`: main system configuration
- `phase4.properties`: AS4 gateway configurations
- `log4j2.xml`: logging configuration

Bellow, a working example of the `application.prperties` file:
```properties
# Database properties
database.datasourceConf.url=jdbc:h2:mem:testdb
database.datasourceConf.driverClassName=org.h2.Driver
database.datasourceConf.username=sa
database.datasourceConf.password=password
database.datasourceConf.initializationMode=always
database.datasourceConf.jpaHibernate.dialectPlatform=org.hibernate.dialect.H2Dialect
database.datasourceConf.jpaHibernate.ddlauto=create-drop
database.datasourceConf.jpaHibernate.generateddl=true
database.datasourceConf.jpaHibernate.namingStrategy=org.hibernate.cfg.ImprovedNamingStrategy
database.datasourceConf.jpaHibernate.showSql=true
database.datasourceConf.jpaHibernate.formatSql=true

# H2 in-memory database console port (default 21080)
h2.console.port=21080

# i18n properties
spring.messages.basename=messages/messages
spring.messages.default_locale=en

# Spring allowing override beans
spring.main.allow-bean-definition-overriding=true

# Servlet encoding
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.force-response=true

# SSL context enabled (true|false)
ssl.context.enabled=false

# SSL configuration (optional when ssl.context.enabled is false, otherwise, it must be configured)
#ssl.keystore.type=
#ssl.keystore.path=
#ssl.keystore.password=
#ssl.truststore.path=
#ssl.truststore.password=

# Global flags for initializer
global.debug = true
global.production = false

# Instance name for logging
global.instancename = dev-from-ide

# DE4A Kafka settings
de4a.kafka.enabled=true
# Enables Kafka connection via HTTP (Only enable HTTP mode if outbound TCP connections are blocked from your internal network)
de4a.kafka.http.enabled=false

# Kafka server address (Eg.: de4a-dev-kafka.egovlab.eu:9092)
de4a.kafka.url=de4a-dev-kafka.egovlab.eu:9092
# Uncomment the following property and remove the above one if HTTP mode is enabled
# de4a.kafka.url=https://de4a-dev-kafka.egovlab.eu

# Establish a topic on kafka tracker - Pattern: de4a-<country-code>-<partner-name> - Eg.: de4a-se-egovlab - (default: de4a-connector)
de4a.kafka.topic=de4a-connector

# toop legacy kafka properties (Do not touch)
toop.tracker.enabled = false

# DSD base URL (Do not modify)
toop.dsd.service.baseurl = http://dsd.dev.exchange.toop.eu

# What AS4 implementation to use?
toop.mem.implementation = phase4

# Our AS4 sending AP endpoint (holodeck)
#toop.mem.as4.endpoint = http://localhost:8083/tc-webapp/as4

# Domibus server endpoint
# domibus.endpoint=

# SMP Client configuration stuff - Do not modify (default values)
smpclient.truststore.type = JKS
smpclient.truststore.path = truststore/de4a-truststore-test-smp-pw-de4a.jks
smpclient.truststore.password = de4a

# Spring As4 gateway  implementation bean(provided: phase4GatewayClient and domibusGatewayClient).Implements eu.toop.as4.client.As4GatewayInterface
as4.gateway.implementation.bean=phase4GatewayClient

# External endpoints
# SMP endpoint Eg.: https://de4a-smp.egovlab.eu/
smp.endpoint=
# IDK endpoint Eg.: https://de4a-dev-idk.egovlab.eu/
idk.endpoint=

# IM response timeout
as4.timeout.miliseconds=30000

# Properties to create the http client connection through a proxy (optional)
#http.proxy.enabled=
#http.proxy.address=
#http.proxy.port=
#http.proxy.non-proxy=
#http.proxyUsername=
#http.proxyPassword=

# Required renamed proxy configuration for BDXRClient (if is needed, only uncomment)
#http.proxyHost=${http.proxy.address}
#http.proxyPort=${http.proxy.port}
#http.nonProxyHosts=${http.proxy.non-proxy}
```
From now on, we will explain the main and most critical configuration:
#### SSL Context (not for AS4) `application.properties`
You can configure secure HTTP connections from the Connector by setting the following property to `true`:
```properties
# SSL context enabled (true|false)
ssl.context.enabled=true
```
In this case you should properly configure the following properties in order to create an SSL context for HTTP communications:
```properties
# SSL configuration (optional when ssl.context.enables is false)
ssl.keystore.type= #(JKS|PKCS12)
ssl.keystore.path= #(Path to keystore where signing private key are included)
ssl.keystore.password= #(Private key password)
ssl.truststore.path= #(JKS truststore)
ssl.truststore.password= #(Truststore password)
```
#### Kafka configuration `application.properties`
In order to send log messages to a kafka server, configure the following parameters:
```properties
# DE4A Kafka settings
de4a.kafka.enabled=true
# Enables Kafka connection via HTTP (Only enable HTTP mode if outbound TCP connections are blocked from your internal network)
de4a.kafka.http.enabled=false

# Kafka server address (Eg.: de4a-dev-kafka.egovlab.eu:9092)
de4a.kafka.url=de4a-dev-kafka.egovlab.eu:9092
# Uncomment the following property and remove the above one if HTTP mode is enabled
# de4a.kafka.url=https://de4a-dev-kafka.egovlab.eu

# toop legacy kafka properties (Do not touch)
toop.tracker.enabled = false
```
**IMPORTANT** - If your server has no access to external domains, the HTTP kafka configuration should be enabled.
To enable HTTP kafka log producer, you only need to set the property to true `de4a.kafka.http.enabled=true` - **Also configure the proper endpoint in order to use HTTP connections**

#### SMP properties `application.properties`
To establish which SMP server will provide the Connector with metadata services, the following properties must be used:
```properties
# SMP Client configuration stuff - Don't touch (default values)
smpclient.truststore.type = JKS
smpclient.truststore.path = truststore/de4a-truststore-test-smp-pw-de4a.jks
smpclient.truststore.password = de4a
..........
# External endpoints
smp.endpoint=https://de4a-smp.egovlab.eu/
```
You can define there your SMP endpoint and truststore which will be used to validate the signature of the responses. - **Do not modify, all consortium SMPs should be validated with the default truststore**.

#### AS4 - TOOP properties `application.properties`
```properties
# What AS4 implementation to use?
toop.mem.implementation = phase4
..........
# Spring As4 gateway  implementation bean(provided: phase4GatewayClient and domibusGatewayClient).Implements eu.toop.as4.client.As4GatewayInterface
as4.gateway.implementation.bean=phase4GatewayClient
..........
# Domibus server endpoint
# domibus.endpoint=
```
#### Proxy properties
Some environments may require perform proxy connections due to security policies or environment limitations¡. That is why the Connector allows to establish HTTP connections via proxy.
```properties
# Properties to create the HTTP client connection through a proxy (optional)
#http.proxy.enabled=
#http.proxy.address=
#http.proxy.port=
#http.proxy.non-proxy=
#http.proxyUsername=
#http.proxyPassword=

# Required renamed proxy configuration for BDXRClient (if needed, only uncomment)
#http.proxyHost=${http.proxy.address}
#http.proxyPort=${http.proxy.port}
#http.nonProxyHosts=${http.proxy.non-proxy}
```
In order to disable proxy configuration, you can either comment the properties or set up `enabled` property to false.
 ```properties
http.proxy.enabled=false
````
**PLEASE NOTE** that in case that you enabled the property (`http.proxy.enabled=true`) above you should uncomment and set up the rest of them. Also uncomment properties regarding to BDXRClient

#### Phase4 properties `phase4.properties`
Parameters used by the built-in Phase4 module of the Connector. Set up the properties above following the commented indications. Some of them are filled in to clarify the content -- **Important** to consider if each property is optional or not (*check out the the in-line comments*).
```properties
# (string) - the absolute path to a local directory to store data
phase4.datapath=
# (boolean) - enable or disable HTTP debugging for AS4 transmissions. The default value is false.
phase4.debug.http=
# (boolean) - enable or disable debug logging for incoming AS4 transmissions. The default value is false.
phase4.debug.incoming=
# (string) (since v2.0.0-rc3) - an optional absolute directory path where the incoming AS4 messages should be dumped to. Disabled by default.
phase4.dump.incoming.path=
# (string) (since v2.0.0-rc3) - an optional absolute directory path where the outgoing AS4 messages should be dumped to. Disabled by default.
phase4.dump.outgoing.path=
# (string) (since v2.0.0-rc3) - the from party ID to be used for outgoing messages. Previous versions need to use toop.mem.as4.tc.partyid - starting from RC3 this property is still used as a fallback)
phase4.send.fromparty.id=egovlab
# (string) (since 2.0.2) - the AS4 To/PartyId/@type value. E.g. urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.toparty.id.type=urn:oasis:names:tc:ebcore:partyid-type:unregistered
# (string) (since 2.0.2) - the AS4 From/PartyId/@type value. E.g. urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.fromparty.id.type=urn:oasis:names:tc:ebcore:partyid-type:unregistered
# (string) - an optional folder, where sent responses should be stored. If this property is not provided, they are not stored
phase4.send.response.folder=
# (string) - the type of the keystore (either "JKS" or "PKCS12" - case insensitive) - defaults to JKS.
phase4.keystore.type=
# (string) - the path to the keystore (can be classpath relative or an absolute file)
phase4.keystore.path=
# (string) - the password to access the keystore
phase4.keystore.password=
# (string) - the alias of the key in the keystore (may be case sensitive)
phase4.keystore.key-alias=
# (string) - the password to access the key in the keystore
phase4.keystore.key-password=
# (string) - the type of the truststore (either "JKS" or "PKCS12" - case insensitive) - defaults to JKS.
phase4.truststore.type=
# (string) - the path to the truststore (can be classpath relative or an absolute file)
phase4.truststore.path=
# (string) -  the password to access the truststore
phase4.truststore.password=
```
#### Logging configuration `log4j2.xml`
The configuration file bellow maintains the logging configuration where you can set the level of each appender, set up log file path, or even include more appenders or configuration.
**Important** - to correctly configure the path of log file. By default it is a relative path to catalina.base (Tomcat server) `${sys:catalina.base}/logs/connector.log`
```xml
<File name="File" fileName="${sys:catalina.base}/logs/connector.log">
	<PatternLayout>
		<pattern>%d %p %C{1.} [%t] %m%n</pattern>
	</PatternLayout>
</File>
```
## Starting up Connector
Once you have all configuration parameters well configured (if not, check the logs to find out the problem), it is time to deploy the component into an applications server.
Once you have deployed the `war` file, there are several **checks to ensure that the deployment was successful**:
- Open Swagger UI browsing: `http://host:port/swagger-ui/`
	- Eg.: [eGovLab Connector](https://de4a-dev-connector.egovlab.eu/swagger-ui/)
- TOOP index page will be at root path: `http://host:port/`
	- Eg.: [eGovLab Connector](https://de4a-dev-connector.egovlab.eu/)
- The Connector will be able to process requests through the following interfaces:
	- `/requestTransferEvidenceIM`
	- `/requestTransferEvidenceUSI`
	- `/lookupRoutingInformation`
	- `/requestTransferEvidenceUSIDT`
- Accessing to in-memory database: `http://host:h2.console.port/`
