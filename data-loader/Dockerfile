FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/data-loader-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
