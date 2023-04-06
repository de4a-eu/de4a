#
# Copyright (C) 2023, Partners of the EU funded DE4A project consortium
#   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
# Author:
#   Austrian Federal Computing Center (BRZ)
#   Spanish Ministry of Economic Affairs and Digital Transformation -
#     General Secretariat for Digital Administration (MAETD - SGAD)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
