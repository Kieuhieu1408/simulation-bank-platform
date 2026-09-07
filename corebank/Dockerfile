FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/target/corebank-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8180
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
