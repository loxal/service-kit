FROM jetty:9-jre8-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD build/libs/ROOT.war webapps

EXPOSE 8080

CMD ["java", "-jar", "-Xmx32m", "/usr/local/jetty/start.jar"]
