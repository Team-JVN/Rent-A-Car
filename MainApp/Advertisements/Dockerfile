FROM maven:3.6.3-ibmjava-8-alpine AS backend

LABEL maintainer="Tim 29"

WORKDIR /usr/src/advertisements
COPY . .
RUN ["mvn", "package", "-DskipTests"]


FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=backend /usr/src/advertisements/target/Advertisements-0.0.1-SNAPSHOT.jar ./

CMD ["java", "-jar", "Advertisements-0.0.1-SNAPSHOT.jar"]
