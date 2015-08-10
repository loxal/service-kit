#!/usr/bin/env bash

# Switch to Java 8 JDK
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
mvn clean package cf:push -P aws-prod