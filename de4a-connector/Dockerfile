FROM tomcat:9-jdk11

ENV CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/urandom"

# Install unzip
RUN apt-get update \
  && apt-get upgrade -y \
  && apt-get install -y unzip \
  && rm -rf /var/lib/apt/lists/*

WORKDIR $CATALINA_HOME/webapps

COPY target/*.war ./connector.war

RUN rm -rf manager host-manager docs examples ROOT && \
    unzip connector.war -d ROOT  && \
    rm -f connector.war
