FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY common-service/pom.xml common-service/
COPY api-gateway/pom.xml api-gateway/
COPY corebank/pom.xml corebank/
COPY money-bank/pom.xml money-bank/
COPY cms cms/

COPY common-service/src common-service/src
COPY corebank/src corebank/src

RUN mvn -B -q -DskipTests package -pl corebank -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo non-root user
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/corebank/target/*.jar app.jar

# Chown jar file
RUN chown spring:spring app.jar

USER spring

EXPOSE 8180
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
