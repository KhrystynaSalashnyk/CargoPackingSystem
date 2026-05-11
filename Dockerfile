# Етап 1: Збірка (використовуємо Eclipse Temurin - це надійний дистрибутив Java)
FROM maven:3.8-eclipse-temurin-11 AS build
COPY . .
RUN mvn clean package -DskipTests

# Етап 2: Запуск
FROM eclipse-temurin:11-jre-focal
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]