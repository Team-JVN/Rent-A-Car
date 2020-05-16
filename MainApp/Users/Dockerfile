FROM maven:3.6.3-ibmjava-8-alpine AS backend

LABEL maintainer="Tim 29"

WORKDIR /usr/src/users
COPY . .
RUN ["mvn", "package", "-DskipTests"]


FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=backend /usr/src/users/target/Users-0.0.1-SNAPSHOT.jar ./

EXPOSE 8080

CMD ["java", "-jar", "Users-0.0.1-SNAPSHOT.jar"]
