@echo off

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.16.8-hotspot
call mvn clean install
if errorlevel 1 goto error

cd de4a-connector
docker build --pull -t de4a/connector:iteration2 -t de4a/connector:latest .
if errorlevel 1 goto error

echo Login to Docker Hub with user phelger
docker login -u phelger
if errorlevel 1 goto error

docker push de4a/connector:iteration2
if errorlevel 1 goto error
docker push de4a/connector:latest
if errorlevel 1 goto error

docker logout

goto end

:error
pause

:end