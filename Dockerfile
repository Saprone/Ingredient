# Use a base image to build the application
FROM openjdk:21-jdk-slim AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build files
COPY pom.xml ./

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a new image to run the application
FROM openjdk:21-jdk-slim

# Create a non-root user to run the application
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar ingredient.jar

# Change ownership of the JAR file
RUN chown appuser:appgroup ingredient.jar

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "ingredient.jar"]