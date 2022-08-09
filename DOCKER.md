# Docker instructions

Pull the official DE4A docker image.

`docker pull de4a/connector:<tag>`

Substitute `<tag>` with the version you want to pull.

To run

`docker run -p 8080:8080 -v ./config/phase4:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/phase4 -v ./config/application.properties:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/application.properties -v ./config/phase4.properties:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/phase4.properties -v /tmp/connector:/tmp de4a/connector:<tag>`

Or use the example docker-compose.yml file with

`docker-compose up -d`

## Folder structure example

For the above docker command you need the following on the HOST machine running the docker container:

a folder named `config/`
a folder named `phase4/` in the `config/` folder.
`application.properties` in the `config/` folder.
`phase4.properties` in the `config/` folder.

The `\*.properties` files are your system specific configuration files where you have changed the relevant properties to suit your system. In the `phase4/` folder you can put your AS4 certificate that will be used by the built in phase4 gateway. Make sure to point the relevant properties in the above files to this folder and the files in there

## Updating the running container to a newer image

With docker-compose run the following to update to a newer image

`docker-compose pull de4aconnector && docker-compose up -d`

Please be advised this only works for the specified `<tag>` in your docker-compose.yml. If you run `latest` you will always get the latest built image.
If you are upgrading between version i.e. 0.1.0 -> 0.2.0 you need to first change the `docker-compose.yml` file to reflect the new version and then run the above command

## Building

Internal instructions to manually build and push Docker images

### Connector

1. Run `mvn clean install` on the main project
2. Goto `de4a-connector` submodule
3. Run `docker build --pull -t de4a/connector:iteration1 .`
4. Run `docker run -d -p 8080:8080 de4a/connector:iteration1`
