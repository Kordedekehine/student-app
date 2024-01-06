FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package  -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/students-app-0.0.1-SNAPSHOT.jar students-app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","students-app.jar"]

