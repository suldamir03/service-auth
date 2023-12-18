# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-alpine

# Копируем JAR-файл Spring приложения в контейнер
COPY auth.jar app.jar

# Запускаем Spring приложение при старте контейнера
CMD ["java", "-jar", "/app.jar"]
