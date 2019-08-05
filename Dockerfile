# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="victor.h.julio.hoyos@accenture.com"

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/sap-wrapper-service.jar"]
