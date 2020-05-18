FROM node:12.7-alpine AS front

LABEL maintainer="Tim 29"

WORKDIR /usr/src/agentapp 
COPY Frontend .
RUN ["npm", "install"]
RUN ["npm", "install","ng-starrating@1.0.11"]
RUN [ "npm", "run", "build", "--prod"]


FROM maven:3.6.3-ibmjava-8-alpine AS appServer

WORKDIR /usr/src/agentapp
COPY Backend .
COPY --from=front /usr/src/agentapp/dist/Frontend ./src/main/resources/static
RUN ["mvn", "package", "-DskipTests"]


FROM openjdk:8-jdk-alpine
EXPOSE 8080
WORKDIR /app

COPY --from=appServer /usr/src/agentapp/target/Rent-A-Car-0.0.1-SNAPSHOT.jar  ./


CMD ["java", "-jar", "Rent-A-Car-0.0.1-SNAPSHOT.jar"]