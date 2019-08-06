# Start with a base image containing Java runtime
FROM xqdocker/ubuntu-openjdk:8
RUN useradd -ms /bin/bash app
USER app
WORKDIR /usr/src/app

# Add Maintainer Info
LABEL maintainer="victor.h.julio.hoyos@accenture.com"

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar
CMD mkdir sap-jco
ADD sap-jco ./sap-jco
# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/home/myuser/sap-wrapper-service.jar"]