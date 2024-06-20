# Base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built Spring Boot application JAR file
COPY build/libs/*.jar /app/myapp.jar

# Expose the port the app runs on
EXPOSE 6500

# Run the application
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
