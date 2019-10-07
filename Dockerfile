FROM maven:3.6-jdk-11-slim AS build
COPY . /usr/
RUN mvn -f /usr/pom.xml clean package

FROM openliberty/open-liberty:microProfile3-ubi-min
USER root
COPY src/main/liberty/config /opt/ol/wlp/usr/servers/defaultServer/
COPY --from=build /usr/target/app-1.0-SNAPSHOT.war /opt/ol/wlp/usr/servers/defaultServer/apps/TraderUI.war
RUN ls config/
RUN ls config/apps/
RUN chown -R 1001:0 config/
USER 1001
RUN configure.sh
