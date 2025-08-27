FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/takeout-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 12345

CMD ["java", "-jar", "app.jar"]