FROM jetty:9-jre8-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD target/service-kit-1.0.0 webapps/ROOT

EXPOSE 8080

CMD ["java", "-jar", "-Xmx32m", "/usr/local/jetty/start.jar"]
