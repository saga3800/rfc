# Start with a base image containing Java runtime
FROM xqdocker/ubuntu-openjdk:8

RUN mkdir -p /usr/src/app

RUN apt-get update && \
    apt-get -y install sudo

ENV user app

RUN useradd -m -d /usr/src/${user} ${user} && \
    chown -R ${user} /usr/src/${user} && \
    adduser ${user} sudo && \
    echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER ${user}

WORKDIR /usr/src/${user}

RUN sudo apt-get -y install curl && \
    sudo apt-get -y install lsb-core && \
    sudo apt-get -y install lsb && \
    sudo apt-get -y upgrade -f


# Add Maintainer Info
LABEL maintainer="victor.h.julio.hoyos@accenture.com"

# The application's jar file
ARG JAR_FILE=target/rfc-wrapper-service-0.0.1-SNAPSHOT.jar
CMD mkdir sap-jco
ADD sap-jco ./sap-jco
# Add the application's jar to the container
ADD ${JAR_FILE} sap-wrapper-service.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/usr/src/app/sap-wrapper-service.jar"]