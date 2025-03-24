FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/
COPY gradle /app/gradle
RUN gradle dependencies --no-daemon
COPY . /app
RUN gradle clean bootJar -x test --no-daemon

FROM openjdk:17-jdk-slim

COPY --from=build /app/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
