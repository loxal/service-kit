#!/usr/bin/env bash

# Switch Java JDK
#export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
#eval "$(docker-machine env default)"

mvn clean package

#docker login
sudo docker build --tag=loxal/service-kit:v1 .
sudo docker push loxal/service-kit:v1
sudo docker rm -f service-kit
sudo docker run -d -p 80:8080 --name service-kit loxal/service-kit:v1
