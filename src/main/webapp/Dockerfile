FROM jetty:9

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD . webapps/ROOT

CMD ["java", "-Djava.io.tmpdir=/tmp/jetty", "-jar", "-Xmx32m", "/usr/local/jetty/start.jar"]