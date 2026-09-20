FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/edificios.war /usr/local/tomcat/webapps/ROOT.war
# Render entrega el puerto en $PORT; Tomcat escucha en 8080 por defecto.
EXPOSE 8080
CMD ["catalina.sh", "run"]
