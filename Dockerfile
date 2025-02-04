# Use an official Maven image with Java 17 as the build environment
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Define build arguments
ARG SPRING_DB_DRIVE
ARG SPRING_DB_URL
ARG SPRING_DB_USER
ARG SPRING_DB_PASSWORD
ARG SPRING_DB_SCHEMA

# Set environment variables using build arguments
ENV SPRING_DB_DRIVE=${SPRING_DB_DRIVE}
ENV SPRING_DB_URL=${SPRING_DB_URL}
ENV SPRING_DB_USER=${SPRING_DB_USER}
ENV SPRING_DB_PASSWORD=${SPRING_DB_PASSWORD}
ENV SPRING_DB_SCHEMA=${SPRING_DB_SCHEMA}

# Build the application
RUN mvn clean package -DskipTests

# Use an official OpenJDK 17 runtime as the base image for the final stage
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8888

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
