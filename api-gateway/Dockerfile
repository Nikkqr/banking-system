FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY /target/api-gateway-1.0-SNAPSHOT.jar api_gateway.jar

ENTRYPOINT ["java", "-jar", "api_gateway.jar"]