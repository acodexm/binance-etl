FROM gradle:8.13-jdk21-alpine as builder

COPY src /usr/app/src
COPY build.gradle /usr/app/
COPY settings.gradle /usr/app/
COPY gradle /usr/app/gradle
COPY gradlew /usr/app/

WORKDIR /usr/app
RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /usr/app/build/libs/*.jar /usr/app/app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
