FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY /target/bank-service-1.0-SNAPSHOT.jar bank_service.jar

ENTRYPOINT ["java", "-jar", "bank_service.jar"]