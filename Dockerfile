# Dockerfile.distroless
FROM bellsoft/liberica-openjdk-alpine:25 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java25-debian13
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]