# Start with a base image containing Java runtime
FROM xqdocker/ubuntu-openjdk:8

# Add Maintainer Info
LABEL maintainer="victor.h.julio.hoyos@accenture.com"

RUN useradd -ms /bin/bash admin
RUN mkdir -p /usr/src/app
RUN mkdir -p /usr/src/app/sap-jco
WORKDIR /usr/src/app
RUN chown -R admin:admin /usr/src/app
RUN chmod 755 /usr/src/app
USER admin

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar

ADD sap-jco ./sap-jco
# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
ENTRYPOINT ["java","-jar","./sap-wrapper-service.jar"]
