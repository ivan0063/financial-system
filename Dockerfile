# Use an official Maven image with Java 17 as the build environment
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

ENV SPRING_DB_DRIVE=org.drive.MariaDb
ENV SPRING_DB_URL=localhost
ENV SPRING_DB_USER=root
ENV SPRING_DB_PASSWORD=root
ENV SPRING_DB_SCHEMA=''

# Build the application
RUN mvn clean package -DskipTests

# Use an official OpenJDK 17 runtime as the base image for the final stage
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]