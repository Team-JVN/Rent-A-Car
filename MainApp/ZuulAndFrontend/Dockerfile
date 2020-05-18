FROM node:12.7-alpine AS front

LABEL maintainer="Tim 29"

WORKDIR /usr/src/zuul 
COPY Frontend .
RUN ["npm", "install"]
RUN ["npm", "install","ng-starrating@1.0.11"]
RUN [ "npm", "run", "build", "--prod"]


FROM maven:3.6.3-ibmjava-8-alpine AS appServer

WORKDIR /usr/src/zuul
COPY Zuul .
COPY --from=front /usr/src/zuul/dist/Frontend ./src/main/resources/static
RUN ["mvn", "package", "-DskipTests"]


FROM openjdk:8-jdk-alpine
EXPOSE 8080
WORKDIR /app
COPY --from=appServer /usr/src/zuul/target/zuul-1.0.0.jar  ./

CMD ["java", "-jar", "zuul-1.0.0.jar"]


# FROM openjdk:8-jdk-alpine

# LABEL maintainer="Tim 29"

# COPY Zuul/entrypoint.sh /entrypoint.sh
# COPY Zuul/target/zuul-1.0.0.jar zuul-1.0.0.jar
# RUN chmod +x /entrypoint.sh

# CMD ["/entrypoint.sh"]
