FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

# Declare the build-time variables
ARG DB_USERNAME
ARG DB_PASSWORD
ARG SPRING_DB_PROD_URL
ARG RABBITMQ_HOST
ARG RABBITMQ_PORT
ARG RABBITMQ_USER
ARG RABBITMQ_PASSWORD
ARG RABBITMQ_QUEUE
ARG IMAGE_NAME
ARG TZ

ENV DB_USERNAME=$DB_USERNAME \
    DB_PASSWORD=$DB_PASSWORD \
    SPRING_DB_PROD_URL=$SPRING_DB_PROD_URL \
    RABBITMQ_HOST=$RABBITMQ_HOST \
    RABBITMQ_PORT=$RABBITMQ_PORT \
    RABBITMQ_USER=$RABBITMQ_USER \
    RABBITMQ_PASSWORD=$RABBITMQ_PASSWORD \
    RABBITMQ_QUEUE=$RABBITMQ_QUEUE \
    IMAGE_NAME=$IMAGE_NAME \
    TZ=$TZ

COPY src /home/app/src
COPY pom.xml /home/app

WORKDIR /home/app

RUN mvn clean package

FROM eclipse-temurin:21.0.2_13-jre-jammy

VOLUME /tmp

EXPOSE 8080

COPY --from=builder /home/app/target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
