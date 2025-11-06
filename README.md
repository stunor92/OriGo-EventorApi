# Eventor API Converter

This Spring Boot web application converts Eventor IOF-XML files to JSON format, which is used in OriGo apps.
The application provides endpoints to convert Eventor IOF-XML files to JSON. You can use tools like curl or Postman to interact with the API.

## Features

- Converts Eventor IOF-XML files to JSON.
- Supports single race events, multi-race events, and relay events.
- Utilizes Kotlin and Java.
- Built with Spring Boot and Maven.

## Prerequisites

- Java 21 or higher
- Maven 3.6.0 or higher

## Configuration

The application uses Spring profiles for configuration:
- `application.yml` - Production configuration using environment variables
- `application-local.yml` - Local development configuration with example values

### Required Environment Variables (Production)

- `POSTGRES_DB` - PostgreSQL database connection URL
- `POSTGRES_USER` - Database username
- `POSTGRES_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT token signing (minimum 32 characters)

### Security Notes

⚠️ **Important**: Never commit actual secrets or production credentials to version control.

- The `application-local.yml` file contains example values for local development only
- Always use environment variables for production deployments
- Change default values in local configuration before using in any shared environment

## Build the project
mvn clean install

## Run the application
mvn spring-boot:run

### Contributing
Fork the repository.
Create a new branch (git checkout -b feature-branch).
Make your changes.
Commit your changes (git commit -am 'Add new feature').
Push to the branch (git push origin feature-branch).
Create a new Pull Request.

### License
This project is licensed under the MIT License - see the LICENSE file for details.