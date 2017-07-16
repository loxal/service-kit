#!/usr/bin/env bash

# Switch Java JDK
#export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
#eval "$(docker-machine env default)"

./mvnw clean package

DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/service-kit:$DOCKER_TAG .
docker push loxal/service-kit:$DOCKER_TAG
docker rm -f service-kit
docker run -d -p 80:8080 --name service-kit loxal/service-kit:$DOCKER_TAG
