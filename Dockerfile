FROM jetty:9

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ENV SERVICE_KIT_VERSION=v1

ADD target/service-kit*/ webapps/ROOT

CMD ["java", "-jar", "-Xmx46m", "/usr/local/jetty/start.jar"]
