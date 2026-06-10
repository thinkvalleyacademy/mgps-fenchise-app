FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -DskipTests package && cp target/*SNAPSHOT.jar /tmp/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /tmp/app.jar /app/app.jar

EXPOSE 8080
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
