@REM
@REM Copyright (C) 2023, Partners of the EU funded DE4A project consortium
@REM   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
@REM Author:
@REM   Austrian Federal Computing Center (BRZ)
@REM   Spanish Ministry of Economic Affairs and Digital Transformation -
@REM     General Secretariat for Digital Administration (MAETD - SGAD)
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM         http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.16.8-hotspot
call mvn clean install
if errorlevel 1 goto error

cd de4a-connector
docker build --pull -t de4a/connector:iteration2 .
if errorlevel 1 goto error

docker stop de4a_conncector_it2 && docker rm de4a_conncector_it2
docker run -d -p 8080:8080 --name de4a_conncector_it2 de4a/connector:iteration2
if errorlevel 1 goto error

goto end

:error
pause

:end
