FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

COPY src /home/app/src
COPY pom.xml /home/app

WORKDIR /home/app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21.0.2_13-jre-jammy

COPY --from=builder /home/app/target/*jar-with-dependencies.jar app/app.jar

WORKDIR /app

CMD ["java", "-jar", "app.jar"]
