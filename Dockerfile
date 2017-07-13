FROM websphere-liberty:microProfile
COPY server.xml /config/server.xml
COPY target/trader-1.0-SNAPSHOT.war /config/apps/TraderUI.war
