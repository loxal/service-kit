#!/usr/bin/env bash

# Switch Java JDK
#export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
#eval "$(docker-machine env default)"

mvn clean package

# docker login -u loxal
docker build --tag=loxal/service-kit:v1 .
docker push loxal/service-kit:v1
docker rm -f service-kit
docker run -d -p 80:8080 --name service-kit loxal/service-kit:v1

docker rmi $(docker images -f "dangling=true" -q) # cleanup, GC for dangling images
