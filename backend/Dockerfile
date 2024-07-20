# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app


# Copy the current directory contents into the container at /app
# COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
COPY build/libs/ .

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","backend-0.0.1-SNAPSHOT.jar"]