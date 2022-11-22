# Part 1:

FROM ubuntu:latest as build

# Install unzip
RUN apt-get update \
  && apt-get upgrade -y \
  && apt-get install -y unzip \
  && rm -rf /var/lib/apt/lists/*

COPY target/de4a-connector.war ./connector.war

RUN unzip connector.war -d /de4a && \
    rm -f /de4a/WEB-INF/classes/*.p12

# Part 2:
    
FROM tomcat:9-jdk11

ENV CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/urandom"

WORKDIR $CATALINA_HOME/webapps
RUN rm -rf manager host-manager docs examples ROOT

COPY --from=build /de4a $CATALINA_HOME/webapps/ROOT
