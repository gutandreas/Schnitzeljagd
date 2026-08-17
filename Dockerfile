# Zweistufiger Bau: gebaut wird im Maven-Image, ausgeliefert nur die JRE mit dem Jar.
# Das haelt das Ergebnis klein und umgeht zugleich das Loopback-Problem des
# Entwicklungsrechners — im Linux-Container laeuft der Build problemlos.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Erst nur die pom.xml kopieren: solange sich die Abhaengigkeiten nicht aendern,
# nutzt Docker fuer diese Schicht den Cache und laedt sie nicht erneut herunter.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/target/schnitzeljagd-*.jar app.jar

# Die H2-Datei liegt im Volume, damit sie einen Neustart des Containers ueberlebt.
ENV SCHNITZELJAGD_DB_PATH=/data/schnitzeljagd
VOLUME ["/data"]

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
