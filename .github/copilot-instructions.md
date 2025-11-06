# GitHub Copilot Instructions for OriGo-EventorApi

## Project Overview
This is a Spring Boot web application that converts Eventor IOF-XML files to JSON format for use in OriGo apps. The application provides REST API endpoints to convert various event data formats including single race events, multi-race events, and relay events.

## Technology Stack
- **Language**: Kotlin (primary) with some Java
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Maven
- **Java Version**: Java 21
- **Key Dependencies**:
  - Spring Boot Starter Web, Data JPA, Actuator
  - JAXB for XML processing (IOF-XML schema)
  - Lombok for boilerplate reduction
  - JWT (jjwt) for authentication
  - PostgreSQL for database
  - SpringDoc OpenAPI for API documentation
  - Mockito and MockK for testing

## Project Structure
```
src/
├── main/
│   ├── kotlin/no/stunor/origo/eventorapi/
│   │   ├── api/          # External API services (Eventor API client)
│   │   ├── data/         # JPA repositories
│   │   ├── model/        # Data models and domain objects
│   │   ├── services/     # Business logic and converters
│   │   └── web/          # REST controllers
│   └── resources/
│       ├── IOF.xsd       # IOF-XML schema for JAXB generation
│       └── application.properties
└── test/
    └── kotlin/           # Unit and integration tests
```

## Build and Test Commands

### Prerequisites
- Java 21 or higher
- Maven 3.6.0 or higher

### Build
```bash
# Clean and compile
mvn clean compile

# Generate JAXB classes from IOF.xsd
mvn generate-sources

# Full build with tests
mvn clean install
```

### Run
```bash
# Run the application
mvn spring-boot:run
```

### Test
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ClassName
```

## Code Conventions

### Kotlin Style
- Use Kotlin idioms and conventions
- Prefer data classes for models
- Use nullable types (`?`) appropriately
- Companion objects for constants
- Use `when` expressions over `if-else` chains
- Follow Spring Boot annotation conventions (`@Service`, `@Repository`, `@RestController`)

### Testing
- Use JUnit 5 for tests
- Use MockK for Kotlin mocking, Mockito for Java
- Test class names should end with `Test`
- Use descriptive test names with backticks: `` `test description` ``
- Mock external dependencies (repositories, services)
- Use reflection sparingly (only when testing private methods is necessary)

### API Design
- REST endpoints should return appropriate HTTP status codes
- Use Spring's `@RestController` and `@RequestMapping`
- Handle exceptions properly
- Document APIs using SpringDoc OpenAPI annotations

## Working with Eventor API
- The `EventorService` class handles all communication with the Eventor API
- Uses JAXB-generated classes from `IOF.xsd` in the `org.iof.eventor` package
- API requires authentication via ApiKey header
- Timeout is set to 6000ms for all requests

## Database
- Uses Spring Data JPA for database access
- PostgreSQL is the database
- Repository interfaces extend `JpaRepository`
- Models use JPA annotations (`@Entity`, `@Table`, etc.)

## Important Notes
1. **JAXB Source Generation**: The build process generates Java classes from `src/main/resources/IOF.xsd`. These are generated into `target/generated-sources/jaxb/` and should not be manually edited.

2. **Kotlin-Java Interop**: The project uses both Kotlin and Java (generated JAXB classes). Be mindful of null safety when working between the two.

3. **XML to JSON Conversion**: The core functionality involves converting Eventor's IOF-XML format to JSON. Converter classes are in the `services/converter` package.

4. **Testing Requirements**: When making changes:
   - Add unit tests for new business logic
   - Mock external dependencies
   - Ensure tests are isolated and repeatable
   - Follow existing test patterns in the codebase

5. **Documentation**: 
   - API documentation is auto-generated via SpringDoc OpenAPI
   - Access at `/swagger-ui.html` when running locally

## Common Tasks

### Adding a New API Endpoint
1. Add method to `EventorService` if calling Eventor API
2. Create/update converter in `services/converter` package
3. Add controller method with appropriate annotations
4. Add unit tests for converter logic
5. Test the endpoint manually or with integration tests

### Adding a New Model
1. Create data class in appropriate `model` subpackage
2. Add JPA annotations if it's an entity
3. Create repository interface if needed
4. Add converter logic if transforming from IOF-XML

### Updating Dependencies
- Check `pom.xml` for current versions
- Update dependency versions carefully to maintain compatibility
- Test thoroughly after updates, especially Spring Boot and Kotlin versions

## Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes with appropriate tests
4. Ensure the build passes: `mvn clean install`
5. Commit with descriptive messages
6. Push and create a Pull Request

## Security Considerations
- API keys should never be committed to the repository
- Use environment variables or configuration for sensitive data
- JWT tokens used for authentication should be handled securely
- Validate and sanitize all external inputs
