FROM maven:3.6.3-ibmjava-8-alpine AS backend

LABEL maintainer="Tim 29"

WORKDIR /usr/src/eureka-servicediscovery
COPY . .
RUN ["mvn", "package", "-DskipTests"]


FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=backend /usr/src/eureka-servicediscovery/target/eureka-1.0.0.jar ./

EXPOSE 8080

CMD ["java", "-jar", "eureka-1.0.0.jar"]


# FROM openjdk:8-jdk-alpine

# LABEL maintainer="Tim 29"

# COPY entrypoint.sh /entrypoint.sh
# COPY target/eureka-1.0.0.jar eureka-1.0.0.jar
# RUN chmod +x /entrypoint.sh

# CMD ["/entrypoint.sh"]
