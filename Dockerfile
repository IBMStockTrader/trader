FROM maven:3.5.2-jdk-11 AS build
COPY src /usr/src
RUN mvn clean package
RUN pwd

FROM openliberty/open-liberty:microProfile3-ubi-min
USER root
COPY src/main/liberty/config config/
RUN mkdir config/apps/
COPY --from=build target/trader-1.0-SNAPSHOT.war config/apps/TraderUI.war
RUN chown -R 1001:0 config/
USER 1001
RUN configure.sh
