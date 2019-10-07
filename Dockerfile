FROM maven:3.6-jdk-11-slim AS build
COPY . /usr/
RUN ls /usr/
RUN mvn -f /usr/pom.xml clean package

FROM openliberty/open-liberty:microProfile3-ubi-min
USER root
COPY src/main/liberty/config config/
RUN mkdir config/apps/
COPY --from=build /usr/target/app-1.0-SNAPSHOT.war config/apps/app.war
RUN ls config/
RUN ls config/apps/
RUN pwd
RUN chown -R 1001:0 config/
USER 1001
RUN configure.sh
