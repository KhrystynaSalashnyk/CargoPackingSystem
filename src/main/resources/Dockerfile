# Використовуємо образ з Java 11
FROM openjdk:11-jre-slim

# Копіюємо згенерований jar-файл у контейнер
COPY target/*.jar app.jar

# Відкриваємо порт 8080
EXPOSE 8080

# Команда для запуску
ENTRYPOINT ["java", "-jar", "/app.jar"]