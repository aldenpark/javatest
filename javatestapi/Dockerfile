# FROM eclipse-temurin:17-jdk
# COPY target/javatestapi-0.0.1-SNAPSHOT.jar app.jar
# ENTRYPOINT ["java", "-Dspring.main.banner-mode=console", "-jar", "/app.jar"]
# EXPOSE 80

# ---- build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r -u 1001 appuser
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
USER appuser
ENTRYPOINT ["java","-XX:+ExitOnOutOfMemoryError","-jar","/app/app.jar"]
