FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app

COPY cms/backend/target/*.jar app.jar

EXPOSE 8182
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
