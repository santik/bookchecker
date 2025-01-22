FROM maven:3.8.5-openjdk-17 as builder

COPY . /home/src

WORKDIR /home/src

RUN mvn clean install

FROM openjdk:17-jdk-slim as finalApp

ENTRYPOINT ["sudo", "mkdir", "/app/"]

WORKDIR app

COPY --from=builder /home/src/target/app.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]