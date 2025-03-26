FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN useradd -m appuser && \
    mkdir -p /app/certs && \
    chown -R appuser:appuser /app

COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

COPY --from=build --chown=appuser:appuser /app/src/main/resources/server.crt /app/certs/
COPY --from=build --chown=appuser:appuser /app/src/main/resources/server.key /app/certs/
RUN chmod 600 /app/certs/server.key

COPY --chown=appuser:appuser zkp-verifier/ /app/zkp-verifier/
RUN cd /app/zkp-verifier && \
    npm install --production && \
    chown -R appuser:appuser /app/zkp-verifier


USER appuser

EXPOSE 8443 4000

HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

COPY --chown=appuser:appuser entrypoint.sh /app/
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]