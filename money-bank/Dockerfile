FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY common-service/pom.xml common-service/
COPY api-gateway/pom.xml api-gateway/
COPY corebank/pom.xml corebank/
COPY money-bank/pom.xml money-bank/

COPY common-service/src common-service/src
COPY money-bank/src money-bank/src

RUN mvn -B -q -DskipTests package -pl money-bank -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/money-bank/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
