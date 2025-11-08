# Test Documentation for OriGo EventorAPI

## Overview

This directory contains comprehensive unit and integration tests for the OriGo EventorAPI project. The test suite validates the conversion of Eventor IOF-XML files to JSON format for OriGo applications.

## Test Structure

```
src/test/
├── kotlin/no/stunor/origo/eventorapi/
│   ├── controller/           # Integration tests for REST controllers
│   │   ├── EventControllerIntegrationTest.kt
│   │   ├── PersonControllerIntegrationTest.kt
│   │   └── UserControllerIntegrationTest.kt
│   ├── data/                 # Persistence tests
│   │   └── MembershipPersistenceTest.kt
│   ├── service/              # Legacy service test location
│   │   └── CalendarServiceTest.kt
│   ├── services/             # Unit tests for service layer
│   │   ├── EventServiceTest.kt
│   │   ├── PersonServiceTest.kt
│   │   ├── UserServiceTest.kt
│   │   └── converter/
│   │       └── CalendarConverterTest.kt
│   └── testdata/             # Test data factories
│       ├── EventorFactory.kt
│       ├── OrganisationFactory.kt
│       └── PersonFactory.kt
└── resources/                # Mock XML data from Eventor API
    ├── calendarService/
    │   ├── EventClass.xml
    │   ├── OrganisationEntries.xml
    │   ├── PersonalResult.xml
    │   └── PersonalStart.xml
    └── eventorResponse/
        ├── eventService/
        │   ├── multiDaysEvent/
        │   ├── oneDayEvent/
        │   └── relayEvent/
        └── personalEventsService/
            ├── notSignedUp/
            ├── personStartTime/
            ├── resultInactive/
            └── signedUp/
```

## Test Categories

### Unit Tests

Unit tests validate individual components in isolation using mocks:

- **Service Tests**: Test business logic in service classes
  - `EventServiceTest`: Tests event retrieval and entry list processing
  - `PersonServiceTest`: Tests person authentication and deletion
  - `UserServiceTest`: Tests user management operations
  - `CalendarConverterTest`: Tests time conversion utilities

### Integration Tests

Integration tests validate REST endpoints using Spring's WebMvcTest:

- **Controller Tests**: Test HTTP endpoints with mocked services
  - `EventControllerIntegrationTest`: Tests event and entry list endpoints
  - `PersonControllerIntegrationTest`: Tests person authentication and deletion
  - `UserControllerIntegrationTest`: Tests user deletion endpoint

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=EventServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=EventServiceTest#"getEvent should retrieve and convert one-day event successfully"
```

### Run Only Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Run Only Unit Tests
```bash
mvn test -Dtest=*Test,!*IntegrationTest
```

## Test Data

### Mock XML Files

The `src/test/resources` directory contains real Eventor API response samples:

- **Event Service Responses**: Various event types (one-day, multi-day, relay)
- **Calendar Service Responses**: Entry lists, start lists, result lists
- **Personal Events**: Different user states (signed up, not signed up, results)

### Test Factories

Factory classes in `testdata/` provide consistent test objects:

- **EventorFactory**: Creates mock Eventor instances
- **OrganisationFactory**: Creates mock organisation data
- **PersonFactory**: Creates mock person/user data

Example usage:
```kotlin
val eventor = EventorFactory.createEventorNorway()
val person = PersonFactory.createTestPerson()
val organisation = OrganisationFactory.createTestOrganisation()
```

## Testing Frameworks

### Dependencies

- **JUnit 5**: Test execution framework
- **MockK**: Kotlin-native mocking library for unit tests
- **SpringMockK**: Spring Boot integration for MockK
- **Spring Test**: Spring Boot testing support
- **JAXB**: XML unmarshalling for loading test data

### Key Annotations

- `@Test`: Marks a test method
- `@BeforeEach`: Setup method run before each test
- `@WebMvcTest`: Loads only web layer for integration tests
- `@MockkBean`: Creates a Spring-managed MockK mock

## Writing New Tests

### Unit Test Pattern

```kotlin
class MyServiceTest {
    private lateinit var dependency: Dependency
    private lateinit var myService: MyService

    @BeforeEach
    fun setup() {
        dependency = mockk()
        myService = MyService()
        
        // Inject mocks using reflection for @Autowired fields
        MyService::class.java.getDeclaredField("dependency").apply {
            isAccessible = true
            set(myService, dependency)
        }
    }

    @Test
    fun `test method should do something`() {
        // Given
        every { dependency.method() } returns result

        // When
        val result = myService.doSomething()

        // Then
        assertEquals(expected, result)
        verify { dependency.method() }
    }
}
```

### Integration Test Pattern

```kotlin
@WebMvcTest(controllers = [MyController::class])
class MyControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var myService: MyService
    
    @MockkBean(relaxed = true)
    private lateinit var jwtInterceptor: JwtInterceptor

    @Test
    fun `endpoint should return expected response`() {
        // Given
        every { myService.getData() } returns testData

        // When & Then
        mockMvc.perform(get("/api/endpoint"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.field").value("expectedValue"))
    }
}
```

### Loading Mock XML Data

```kotlin
val xmlData = JAXBContext.newInstance(Event::class.java)
    .createUnmarshaller()
    .unmarshal(File("src/test/resources/eventorResponse/eventService/oneDayEvent/Event.xml")) as Event
```

## Test Coverage

Current test coverage (as of latest commit):

- **EventService**: ~60% (critical paths covered)
- **PersonService**: ~80% (all major flows covered)
- **UserService**: 100% (simple service, fully covered)
- **CalendarConverter**: 100% (utility methods covered)
- **Controllers**: Integration smoke tests for all endpoints

## Best Practices

1. **Test Naming**: Use descriptive names with backticks for readability
   ```kotlin
   @Test
   fun `method should do expected thing when condition is met`()
   ```

2. **Given-When-Then**: Structure tests clearly
   ```kotlin
   // Given - setup and mocks
   // When - execute the code under test
   // Then - verify expectations
   ```

3. **Minimal Mocking**: Only mock external dependencies and collaborators

4. **Test Data**: Use factories for consistent test objects

5. **Isolation**: Each test should be independent and repeatable

6. **Assertions**: Use specific assertions with clear failure messages

## Common Issues

### Java Version

Tests require Java 21. Set `JAVA_HOME` if needed:
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### Reflection Warnings

MockK may show warnings about dynamic agent loading. These are informational and don't affect test execution.

### JWT Interceptor in Integration Tests

Integration tests mock the JWT interceptor to bypass authentication. If you see 401 errors, ensure the interceptor is properly mocked with `@MockkBean(relaxed = true)`.

## Future Improvements

Potential areas for expanded test coverage:

- [ ] Converter classes (EventConverter, OrganisationConverter, etc.)
- [ ] Error handling edge cases
- [ ] CalendarService additional scenarios
- [ ] EventListController integration tests
- [ ] End-to-end tests with TestContainers
- [ ] Performance tests for large XML files
- [ ] Mutation testing with PIT

## Resources

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [MockK Documentation](https://mockk.io/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Kotlin Test Documentation](https://kotlinlang.org/docs/jvm-test-using-junit.html)
