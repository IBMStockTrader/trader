FROM websphere-liberty:microProfile
COPY server.xml /config/server.xml
COPY build/libs/trader.war /config/apps/TraderUI.war
