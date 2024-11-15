# Start with a base image containing Java runtime
FROM xqdocker/ubuntu-openjdk:8

WORKDIR /usr/src/app/
# Add Maintainer Info

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar
CMD mkdir sap-jco
ADD sap-jco ./sap-jco
# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
#ENTRYPOINT ["java","-jar","/usr/src/app/sap-wrapper-service.jar"]
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n","-jar","/usr/src/app/sap-wrapper-service.jar"]