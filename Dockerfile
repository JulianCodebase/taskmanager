# -------- Stage 1: Build the Spring Boot app --------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory inside the builder container
WORKDIR /app

# Copy all project files (pom.xml + src folder, etc.)
COPY . .

# Package the application (skip tests for speed)
RUN mvn clean package -DskipTests

# -------- Stage 2: Create the final image --------
FROM eclipse-temurin:17-jdk

# Set working directory in the final image
WORKDIR /app

# Copy the jar built in the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]