FROM eclipse-temurin:17-jdk
COPY target/javatestapi-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dspring.main.banner-mode=console", "-jar", "/app.jar"]
EXPOSE 8080