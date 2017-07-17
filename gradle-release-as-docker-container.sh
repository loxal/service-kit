#!/usr/bin/env bash

#./gradlew clean build -x test
#./gradlew clean build prepareArchiveWebApp prepareInplaceWebApp -x test; ls build/inplaceWebapp/WEB-INF
./gradlew clean war

DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/service-kit:$DOCKER_TAG . -f Dockerfile.gradle.docker
#docker push loxal/service-kit:$DOCKER_TAG
docker rm -f service-kit
docker run -d -p 80:8080 --name service-kit loxal/service-kit:$DOCKER_TAG

docker exec -it service-kit sh