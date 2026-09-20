FROM gradle:8.10-jdk17 AS build
WORKDIR /workspace
COPY . .
ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:bootJar -x test

FROM eclipse-temurin:17-jre
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /workspace/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]