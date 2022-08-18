# DE4A - CONNECTOR `Iteration 2`

Connector component. Check out following instructions and descriptions.

## de4a-connector

### `package`

Checkout the technical documentation on [the Wiki page](https://wiki.de4a.eu/index.php/DE4A_Connector)

### Public REST API

#### Requests

* **`/request/im`** - As DR, take an IM request (`RequestExtractMultiEvidenceIMType`) and send it with AS4 to DT. Returns a generic synchronous response (`ResponseErrorType`).
* **`/request/usi`** - As DR, take a USI request (`RequestExtractMultiEvidenceUSIType`) and send it with AS4 to DT. Returns a generic synchronous response (`ResponseErrorType`).
* **`/request/lu`** - As DR, take a USI request (`RequestExtractMultiEvidenceLUType`) and send it with AS4 to DT. Returns a generic synchronous response (`ResponseErrorType`).
* **`/request/subscription`** - As DR, take a USI request (`RequestEventSubscriptionType`) and send it with AS4 to DT. Returns a generic synchronous response (`ResponseErrorType`).
* **`/requestTransferEvidenceIM`** - This is the backwards compatibility layer for Iteration 1. As DR, take an Iteration 1 IM request (`RequestTransferEvidenceUSIIMDRType`) and send it with AS4 to DT. Wait synchronously until the DR receives a matching response from DT. Return an Iteration 1 IM response (`ResponseTransferEvidenceType`). It times out after 60 seconds.

#### Responses

* **`/response/usi/redirectUser`** - As DT, takes a Redirect User response (`RedirectUserType`) and send it with AS4 to DR. Returns a generic synchronous response (`ResponseErrorType`).
* **`/response/evidence`** - As DT, takes an Evidence response (`ResponseExtractMultiEvidenceType`) and send it with AS4 to DR. Returns a generic synchronous response (`ResponseErrorType`).
* **`/response/subscription`** - As DT, takes an Subscription response (`ResponseEventSubscriptionType`) and send it with AS4 to DR. Returns a generic synchronous response (`ResponseErrorType`).

#### Events

* **`/event/notification`** - Takes an event notification (`EventNotificationType`). Returns a generic synchronous response (`ResponseErrorType`).

#### Service APIs

* **`/service/ial/{cot}`** - Query the IAL for provided Canonical Object Types (COTs). Multiple COTs can be separated by `,`. No ATU restrictions are applied.
* **`/service/ial/{cot}/{atu}`** - Query the IAL for provided Canonical Object Types (COTs) but only for the ones in the specified ATU. Multiple COTs can be separated by `,`.
* **`/service/reload-addresses`** - Reload the internal address list for forwarding to DE and DO from the backend file.

### Configuration

#### Application properties

Global application properties definitions (including AS4 properties):

```sh
~/de4a/de4a-connector/src/main/resources/application.properties
```

#### External addresses configuration

Configuration meant to **define the DE/DOs endpoints** where the components expect the delivered messages from the Connector.
The structure is divided in two principal groups and in turn into different types according with the component nature regarding the interactions patterns:

* **Owner Addresses**
   * **im**: endpoint where the Connector will deliver an IM message
   * **usi**: endpoint where the Connector will deliver an USI message
   * **lu**: endpoint where the Connector will deliver an LookUp message
   * **sn**: endpoint where the Connector will deliver an Subscription message
* **Evaluator Addresses**
   * **response**: endpoint where the Connector will deliver a response message
   * **subscription_resp**: endpoint where the Connector will deliver a subscription response message
   * **redirect**: endpoint where the Connector will deliver a user redirection message
   * **notification**: endpoint where the Connector will deliver a notification message

The configuration file is located at:

```sh
~/de4a/de4a-connector/src/main/resources/de-do.json
```

#### Logging config

Supported by the logging framework Log4J2, multiple parameters can be configured by editing the following config file:

```sh
~/de4a/de4a-connector/src/main/resources/log4j2.xml
```

## Installation

You should be able to compile entire packages from the parent POM file:

```sh
mvn clean install
```

It is also possible to compile each package separately by browsing to the folder and running the command above.
It requires Java 11 and Maven 3.x (latest).

### Maven packages

The Connector is deployed to Maven Central and is available via the following coordinates, replacing `x.y.z` with the real version:

```xml
<dependency>
  <groupId>eu.de4a</groupId>
  <artifactId>de4a-connector</artifactId>
  <version>x.y.z</version>
  <type>war</type>
</dependency>
```

### Docker

The Docker images are not yet ready.
See DOCKER.md for details

### Package

The compilation process will packaging the project into a `.war` file located on `/target/` path, which should be deployable on any applications server.

This project leverages other DE4A applications and libraries:
* **`de4a-commons`**
    * Content: Utilities and resources based on the model and schemas defined at the [Schemas project](https://github.com/de4a-wp5/xml-schemas)
    * Source: https://github.com/de4a-wp5/de4a-commons
    * Deployed to Maven Central: https://search.maven.org/search?q=g:eu.de4a

* **`ial-service`**
    * Content: Utilities and resources related to the IAL data model
    * Source: https://github.com/de4a-wp5/ial-service
    * Deployed to Maven Central: https://search.maven.org/search?q=g:eu.de4a.ial

* **`de4a-connector-ng`**
    * Content: A TOOP derived Connector infrastructure that contains tools for Dynamic Discovery, IAL calling and the AS4 message exchange
    * Source: https://github.com/de4a-wp5/de4a-connector-ng
    * Deployed to Maven Central: https://search.maven.org/search?q=g:com.helger.dcng

## Connector configuration guide

For a correct configuration of the Connector, three main property files must be considered:
- `reference.properties`: Part of the system configuration that should not be changed.
- `application.properties`: main system configuration. Properties configured here, have precedence over the ones in `reference.properties`.
- `de-do.json`: DE/DOs addresses for delivering messages
- `log4j2.xml`: logging configuration

Lets review relevant aspects of the overall configuration:

### Kafka configuration `application.properties`

In order to send log messages to a kafka server, configure the following parameters:

```properties
de4a.kafka.enabled = true
# Enables the standard logging separately of the Kafka messages. It is necessary for print metrics messages - (default: true)
de4a.kafka.logging.enabled = true
# Enables Kafka connection via HTTP (Only enable HTTP mode if outbound TCP connections are blocked from your internal network)
de4a.kafka.http.enabled = false
# Kafka server address
de4a.kafka.url = de4a.simplegob.com:9092
# Uncomment the following property and remove the above one if HTTP mode is enabled
#de4a.kafka.url=https://de4a.simplegob.com/kafka-rest/
# Establish a topic on kafka tracker - Pattern: de4a-<country-code>-<partner-name> - Eg.: de4a-es-sgad - (default: de4a-connector)
de4a.kafka.topic=de4a-connector
# Logging metrics messages prefix - Default: DE4A METRICS
log.metrics.prefix=DE4A METRICS
```

**IMPORTANT** - If your server has no access to external domains, the HTTP kafka and proxy configuration should be enabled.
To enable HTTP kafka log producer, you only need to set the property to true `de4a.kafka.http.enabled=true` - **Also configure the proper endpoint in order to use HTTP connections**

It is important to mention the property `de4a.kafka.logging.enabled`, used to enable the file log printing for each kafka message sent, that property could be enabled even when the `de4a.kafka.enabled=false`, just for write the log at the different appenders configured in the log4j2 configuration file.

### SMP/SML properties `reference.properties`

To establish which SMP server will provide the Connector with metadata services, the following properties must be used:

```properties
# SMP stuff is always the same for the pilots
de4a.smp.http.useglobalsettings = true
de4a.smp.usedns = true
de4a.smp.tls.trustall = true
de4a.smp.sml.name = SMK DE4A
de4a.smp.sml.dnszone = de4a.acc.edelivery.tech.ec.europa.eu.
de4a.smp.sml.serviceurl = https://acc.edelivery.tech.ec.europa.eu/edelivery-sml
de4a.smp.sml.clientcert = true

# SMP truststore for validating messages
smpclient.truststore.type = jks
smpclient.truststore.path = truststore/de4a-truststore-smp-v3-pw-de4a.jks
smpclient.truststore.password = de4a
```

### AS4 properties `application.properties` and `reference.properties`

```properties
# What AS4 implementation to use?
de4a.me.implementation = phase4
```

Parameters used by the built-in Phase4 module of the Connector. Set up the properties above following the commented indications. Some of them are filled in to clarify the content -- **Important** to consider if each property is optional or not (*check out the the in-line comments*).

```properties
# The from party ID to be used for outgoing messages
phase4.send.fromparty.id = de4a-test
# To party ID to be used for outgoing messages. Configure it IN CASE OF USING Domibus without dynamic participant 
# discovery or in the phase4 side in a mixed AS4 implementations phase4 <-> Domibus
# phase4.send.toparty.id=
# The AS4 To/PartyId/@type value. Default: urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.toparty.id.type=urn:oasis:names:tc:ebcore:partyid-type:unregistered
# The AS4 From/PartyId/@type value. Default: urn:oasis:names:tc:ebcore:partyid-type:unregistered
phase4.send.fromparty.id.type=urn:oasis:names:tc:ebcore:partyid-type:unregistered
# An OPTIONAL folder, where sent responses should be stored. If this property is not provided, they are not stored
phase4.send.response.folder = 

# The absolute path to a local directory to store data
phase4.datapath = 
# Enable or disable HTTP debugging for AS4 transmissions. The default value is false.
phase4.debug.http = false
# Enable or disable debug logging for incoming AS4 transmissions. The default value is false.
phase4.debug.incoming = false
# An OPTIONAL absolute directory path where the incoming AS4 messages should be dumped to. Disabled by default.
phase4.dump.incoming.path = 
# An OPTIONAL absolute directory path where the outgoing AS4 messages should be dumped to. Disabled by default.
phase4.dump.outgoing.path = 

# AS4 keystore for signing/decrypting
phase4.keystore.type = 
phase4.keystore.path = 
phase4.keystore.password = 
phase4.keystore.key-alias = 
phase4.keystore.key-password = 

# AS4 truststore for validating
phase4.truststore.type = jks
phase4.truststore.path = truststore/de4a-truststore-as4-v3-pw-de4a.jks
phase4.truststore.password = de4a
```

### Logging configuration `log4j2.xml`

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
- Connector index page will be at root path: `http://host:port/`
	- Eg.: [UM Connector](https://de4a-connector.informatika.uni-mb.si/)

# News and Noteworthy

* v0.2.3 - 2022-08-18
    * The `/service/ial` queries no longer require the `application/xml` Content Type
    * Updated the Iteration 1 code bindings to avoid conflicting paths (de4a-iem 0.1.13)
    * Fixed bugs in the default page of the Connector - now all properties are shown
    * Made the IAL remote query more resilient
* v0.2.2 - 2022-08-16
    * Updated to de4a-commons 0.2.12
    * Updated to de4a-connector-ng 0.2.7 with improved configurability
    * Using a consistent logging towards Kafka Tracker
* v0.2.1 - 2022-07-06
    * Updated to de4a-commons 0.2.8
    * Improved the Iteration 1 backwards compatibility interface for the DO side as well
* v0.2.0 - 2022-05-20
    * This is the first version of the Iteration 2 Connector. It's not the final version, but a good starting point
