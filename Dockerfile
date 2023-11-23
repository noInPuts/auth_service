FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory
WORKDIR /usr/src/app

# Copy the project files to the container
COPY pom.xml .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Create a lightweight image with only the JAR file and Java
FROM openjdk:17-slim

# Set the working directory
WORKDIR /usr/src/app

# Copy the JAR file from the build image to this image
COPY --from=build /usr/src/app/target/auth_service.jar .

# Specify the command to run your application
CMD ["java", "-jar", "auth_service.jar"]