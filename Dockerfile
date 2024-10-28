# Use Maven to build the application
FROM maven:3.9.9-eclipse-temurin-21 as builder
# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Use a lightweight JDK base image for the runtime
FROM eclipse-temurin:21-jre

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar /app.jar

# Run the application
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]