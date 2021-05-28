FROM tomcat:9-jdk11

ARG WAR_NAME=de4a-idk-mock.war

#create tc webapp folder
WORKDIR $CATALINA_HOME/webapps

ENV CATALINA_OPTS="$CATALINA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -Djava.security.egd=file:/dev/urandom"

COPY target/${WAR_NAME} ./

RUN rm -fr manager host-manager docs examples ROOT && \
    unzip $WAR_NAME -d ROOT  && \
    rm -fr $WAR_NAME

