FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*  # Reduce image size

RUN useradd -m appuser && \
    mkdir -p /app/resources /app/zkp-verifier && \
    chown -R appuser:appuser /app

WORKDIR /app

COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

COPY --chown=appuser:appuser src/main/resources/server.p12 /app/resources/server.p12

COPY --chown=appuser:appuser zkp-verifier/ /app/zkp-verifier/
WORKDIR /app/zkp-verifier
RUN npm install && chown -R appuser:appuser /app/zkp-verifier

USER appuser

WORKDIR /app
EXPOSE 8443 4000

CMD ["sh", "-c", "(cd /app/zkp-verifier && node server.js &) && java -jar app.jar"]
