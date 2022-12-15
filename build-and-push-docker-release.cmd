@echo off

:: The version to release
set VER=0.3.2

cd de4a-connector
docker build --pull -t de4a/connector:%VER% .
if errorlevel 1 goto error

echo Login to Docker Hub with user phelger
docker login -u phelger
if errorlevel 1 goto error

docker push de4a/connector:%VER%
if errorlevel 1 goto error

docker logout

goto end

:error
pause

:end
