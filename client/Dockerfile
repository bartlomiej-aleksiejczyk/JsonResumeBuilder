FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

COPY src /home/app/src
COPY pom.xml /home/app

WORKDIR /home/app

RUN mvn clean package

FROM eclipse-temurin:21.0.2_13-jre-jammy

VOLUME /tmp

EXPOSE 8080

COPY --from=builder /home/app/target/*.jar app.jar
COPY --from=builder / .
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
