#FROM java:8
#FROM jetty
#FROM jetty:9.3-jre8
#FROM jetty:latest
FROM tomcat:8.0.29-jre8
#FROM gcr.io/_b_jenkins_images/jetty9-compat:0.1

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD . /app
RUN ls -lash /
RUN ls -lash
RUN ls -lash webapps
RUN ls -lash webapps/ROOT
RUN ls -lash bin
#RUN cat start.d/deploy.ini
#RUN cat /docker-entrypoint.bash
RUN ls -lash /app
RUN java -version
#RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,stats
#CMD ["bin/startup.sh"]
RUN bin/startup.sh
######################
