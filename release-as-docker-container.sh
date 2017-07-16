#!/usr/bin/env bash

./mvnw clean package

DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/service-kit:$DOCKER_TAG .
docker push loxal/service-kit:$DOCKER_TAG
docker rm -f service-kit
docker run -d -p 80:8080 --name service-kit loxal/service-kit:$DOCKER_TAG
