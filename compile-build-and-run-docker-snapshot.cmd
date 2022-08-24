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
