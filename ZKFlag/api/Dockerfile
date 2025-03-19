FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY zkp-verifier/ /app/zkp-verifier/

RUN cd /app/zkp-verifier && npm install

EXPOSE 8080 4000

CMD ["sh", "-c", "(cd /app/zkp-verifier && node server.js &) && java -jar app.jar"]