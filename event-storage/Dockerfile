FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY /target/event-storage-1.0-SNAPSHOT.jar event_storage.jar

ENTRYPOINT ["java", "-jar", "event_storage.jar"]