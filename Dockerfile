# Start with a base image containing Java runtime
FROM xqdocker/ubuntu-openjdk:8

# Add Maintainer Info
LABEL maintainer="victor.h.julio.hoyos@accenture.com"

RUN mkdir -p /usr/src/app
USER spring
WORKDIR /usr/src/app

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar
CMD mkdir /usr/src/app/sap-jco
ADD sap-jco ./sap-jco
# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
ENTRYPOINT ["java","-jar","./sap-wrapper-service.jar"]
