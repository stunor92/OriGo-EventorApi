# Use Maven to build the application
FROM maven:3-eclipse-temurin-23-alpine as builder
# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Use a lightweight JDK base image for the runtime
FROM bellsoft/liberica-openjre-alpine:23

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar /app.jar

# Run the application
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]