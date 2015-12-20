#!/usr/bin/env bash

# Switch to Java 8 JDK
# export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

# mvn clean package cf:push -P cf-aws-stage # CloudFoundry
# mvn clean package appengine:update # GAE (managed)
gcloud config set project rest-kit-loxal
export DOCKER_TLS_VERIFY=0
mvn clean validate gcloud:deploy # gcloud (managed / unmanaged)
