
# DE4A - CONNECTOR `Iteration1`
Connector component. Check out following instructions and descriptions.

## de4a-commons
#### `package`
Conceived as a library that maintains:
- Utils and general purpose methods
- Common classes

## de4a-idk
#### `package`
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
#### `package`
Checkout the technical documentation on [DE4A D5.5 First Release of DE4A Common Components v1.0.1](https://newrepository.atosresearch.eu/remote.php/webdav/DE4A-Project/06%20Workpackages/WP5%20Components%20design/D5.5%20First%20Release%20of%20DE4A%20Common%20Components/DE4A%20D5.5%20First%20Release%20of%20DE4A%20Common%20Components%20v1.0.1.docx)
### API doc
Once you deploy a Connector instance, it will be able to access to Swagger UI browsing:

```sh
http://connector-endpoint:port/swagger-ui/
```

Even so, the API definition is published at:
[Public Swagger API Connector](https://app.swaggerhub.com/apis/de4a/Connector/0.1.1)



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

#### Logging config

```sh
~/de4a/de4a-connector/src/main/resources/log4j2.xml
```

## Installation
You should be able to compile entire packages from the parent POM file:

```sh
mvn clean install
```

It is also possible to compile each package separately by browsing to the folder and running the command above.
#### Package
The compilation process will packaging the project into a `.war` file located on `/target/` path, which should be deployable on any applications server. If you compile the parent pom, the IDK and Connector target paths will be created with their corresponding `war` files.

#### de4a-commons `v0.1.11`
[de4a-commons](https://github.com/de4a-wp5/de4a-commons/tree/iteration1) project is now on maven central [OSS Sonatype repository](https://search.maven.org/search?q=g:eu.de4a)

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

# H2 in-memory database console port (just uncomment it under controlled circumstances)
# h2.console.port=21080

# i18n properties
spring.messages.basename=messages/messages
spring.messages.default_locale=en

# Spring allowing override beans
spring.main.allow-bean-definition-overriding=true

# Charset encoding
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

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
# Enables the standard logging separately of the Kafka messages. It is neccessary for print metrics messages - (default: true)
de4a.kafka.logging.enabled=true
# Enables Kafka connection via HTTP (Only enable HTTP mode if outbound TCP connections are blocked from your internal network)
de4a.kafka.http.enabled=false

# Kafka server address (Eg.: de4a-dev-kafka.egovlab.eu:9092)
de4a.kafka.url=de4a-dev-kafka.egovlab.eu:9092
# Uncomment the following property and remove the above one if HTTP mode is enabled
# de4a.kafka.url=https://de4a-dev-kafka.egovlab.eu

# Establish a topic on kafka tracker - Pattern: de4a-<country-code>-<partner-name> - Eg.: de4a-se-egovlab - (default: de4a-connector)
de4a.kafka.topic=de4a-connector

# Logging metrics messages prefix - Default: DE4A METRICS
log.metrics.prefix=DE4A METRICS

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

# SML configuration
sml.service.id = de4a
sml.certificate.required = true

# SMK (test environment with test PKI)
#sml.displayname = SMK [DE4A]
#sml.dnszone = de4a.acc.edelivery.tech.ec.europa.eu.
#sml.managementservice.endpoint = https://acc.edelivery.tech.ec.europa.eu/edelivery-sml

# SML (production environment with production Telesec PKI)
sml.displayname = SML [DE4A]
sml.dnszone = de4a.edelivery.tech.ec.europa.eu.
sml.managementservice.endpoint = https://edelivery.tech.ec.europa.eu/edelivery-sml

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

Eventually, due to your environment configuration and structure, you need to disabled the SSL context property, in that case, you should configure the corresponding JVM parameters to specify the truststore, keystore, etc. or the further actions depending of your environment configuration.
#### Kafka configuration `application.properties`
In order to send log messages to a kafka server, configure the following parameters:

```properties
# DE4A Kafka settings
de4a.kafka.enabled=true
# Enables the standard logging separately of the Kafka messages. It is neccessary for print metrics messages - (default: true)
de4a.kafka.logging.enabled=true
# Enables Kafka connection via HTTP (Only enable HTTP mode if outbound TCP connections are blocked from your internal network)
de4a.kafka.http.enabled=false

# Kafka server address (Eg.: de4a-dev-kafka.egovlab.eu:9092)
de4a.kafka.url=de4a.simplegob.com:9092
# Uncomment the following property and remove the above one if HTTP mode is enabled
# de4a.kafka.url=https://de4a.simplegob.com/kafka-rest/

# toop legacy kafka properties (Do not touch)
toop.tracker.enabled = false
```

**IMPORTANT** - If your server has no access to external domains, the HTTP kafka and proxy configuration should be enabled.
To enable HTTP kafka log producer, you only need to set the property to true `de4a.kafka.http.enabled=true` - **Also configure the proper endpoint in order to use HTTP connections**  
It is important to mention the property `de4a.kafka.logging.enabled`, used to enable the file log printing for each kafka message sent, that property could be enabled even when the `de4a.kafka.enabled=false`, just for write the log at the different appenders configured in the log4j2 configuration file.

#### SMP properties `application.properties`
To establish which SMP server will provide the Connector with metadata services, the following properties must be used:

```properties
# SMP Client configuration stuff - Do not touch (default values)
smpclient.truststore.type = JKS
smpclient.truststore.path = truststore/de4a-truststore-smp-it2-pw-de4a.jks
smpclient.truststore.password = de4a
..........
# External endpoints
#smp.endpoint=
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
#http.proxy.non-proxy= ("|" delimiter)
#http.proxyUsername=
#http.proxyPassword=

# Required renamed proxy configuration for BDXRClient (if is needed, duplicate the values)
#http.proxyHost=
#http.proxyPort=
#http.nonProxyHosts=
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
phase4.debug.http=false
# (boolean) - enable or disable debug logging for incoming AS4 transmissions. The default value is false.
phase4.debug.incoming=false
# (string) - an optional absolute directory path where the incoming AS4 messages should be dumped to. Disabled by default.
phase4.dump.incoming.path=
# (string) - an optional absolute directory path where the outgoing AS4 messages should be dumped to. Disabled by default.
phase4.dump.outgoing.path=
# (string) - the from party ID to be used for outgoing messages. Previous versions need to use toop.mem.as4.tc.partyid - starting from RC3 this property is still used as a fallback)
phase4.send.fromparty.id=
# (string) - to party ID to be used for outgoing messages. Configure it in case of using Domibus without dynamic participant discovery or in the phase4 side in a mixed AS4 implementations phase4<->Domibus
phase4.send.toparty.id=
# (string) - the AS4 To/PartyId/@type value. E.g. urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.toparty.id.type=
# (string) - the AS4 From/PartyId/@type value. E.g. urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.fromparty.id.type=
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
phase4.truststore.type=JKS
# (string) - the path to the truststore (can be classpath relative or an absolute file)
phase4.truststore.path=truststore/de4a-truststore-as4-it2-pw-de4a.jks
# (string) - the password to access the truststore
phase4.truststore.password=de4a
```

#### Logging configuration `log4j2.xml`
The configuration file bellow maintains the logging configuration where you can set the level of each appender, set up log file path, or even include more appenders or configuration.
**Important** - to correctly configure the path of log file. By default it is a relative path to catalina.base (Tomcat server) `${sys:catalina.base}/logs/connector.log`

```xml
<RollingFile name="rollingFile"
	fileName="${sys:catalina.base}/logs/connector.log"
	filePattern="${sys:catalina.base}/logs/history/connector.%d{dd-MMM}.log.gz"
	ignoreExceptions="false">
	<PatternLayout>
		<Pattern>>$${ctx:metrics:-}[%date{ISO8601}][%-5level][$${ctx:logcode:-}][DE4A-CONNECTOR][$${ctx:origin:-}][$${ctx:destiny:-}] %msg -- %location [%thread]%n</Pattern>
	</PatternLayout>
	<Policies>
		<OnStartupTriggeringPolicy />
		<SizeBasedTriggeringPolicy size="30 MB" />
	</Policies>
	<DefaultRolloverStrategy max="5" />
</RollingFile>
```

Also, in the `application.properties` there is another property related with the logging.

```properties
# Logging metrics messages prefix - Default: DE4A METRICS
log.metrics.prefix=DE4A METRICS
```

It is used to include a prefix on each logging line written by the Kafka logging that could be useful to parse and filter the lines with metrics information.

## Starting up Connector
Once you have all configuration parameters well configured (if not, check the logs to find out the problem), it is time to deploy the component into an applications server.
Once you have deployed the `war` file, there are several **checks to ensure that the deployment was successful**:
- Open Swagger UI browsing: `http://host:port/swagger-ui/`
	- Eg.: [UM Connector](https://de4a-connector.informatika.uni-mb.si/swagger-ui/)
- TOOP index page will be at root path: `http://host:port/`
	- Eg.: [UM Connector](https://de4a-connector.informatika.uni-mb.si/)
- The Connector will be able to process requests through the following interfaces:
	- `/requestTransferEvidenceIM`
	- `/requestTransferEvidenceUSI`
	- `/lookupRoutingInformation`
	- `/requestTransferEvidenceUSIDT`

## News and Noteworthy

* v0.1.6 - 2022-04-27
    * Fixing a `NullPointerException` in error case
    * Updated to latest H2 version 2.1.212
* v0.1.5 - 2022-04-01 (no April 1st joke)
    * Updated Spring and Spring Boot dependencies due to CVE-2022-22965 - details at https://tanzu.vmware.com/security/cve-2022-22965
* v0.1.4 - 2022-03-30
    * Changed the default trust stores to use the Production Telesec certificates (filenames `de4a-truststore-as4-it2-pw-de4a.jks` and `de4a-truststore-smp-it2-pw-de4a.jks`)
    * Changed the default configuration files to use the new truststore
    * The SML configuration was changed to use the production SML instead of the testing SMK
    * The logging around the SMP lookup was improved

## Building a release

1. Make sure to update "News and Noteworthy" in this document. Commit and push it.
1. Call `mvn clean install`
1. Create a tag with the name `vX.Y.Z` where X is the major version, Y the minor version and Z the patch or micro version
1. Push this tag to GitHub
1. Create a new release on GitHub using the just created tag
1. The Docker image is built and pushed automatically by a Jenkins job
