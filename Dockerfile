# Use a base image with Java 17
FROM eclipse-temurin:17-jdk

# Set the working directory in the container
WORKDIR /app

# Add the built jar file to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]