# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine


# Install fonts
RUN apk add --no-cache msttcorefonts-installer fontconfig
RUN update-ms-fonts

# Set the working directory to /app
WORKDIR /app


# Copy the current directory contents into the container at /app
# COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
COPY build/libs/ .

# Make port 8080 available to the world outside this container
EXPOSE 8080

ENV JAVA_OPTS="-server --add-exports java.desktop/sun.font=ALL-UNNAMED"

# Run the jar file
ENTRYPOINT ["java","-jar","backend-0.0.1-SNAPSHOT.jar"]