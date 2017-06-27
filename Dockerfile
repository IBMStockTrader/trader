FROM websphere-liberty:microProfile
COPY server.xml /config/server.xml
COPY TraderUI.war /config/apps/TraderUI.war
