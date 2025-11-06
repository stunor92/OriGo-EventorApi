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

## Build the project
mvn clean install

## Run the application
mvn spring-boot:run

## Contributing

This project follows [Conventional Commits](https://www.conventionalcommits.org/) specification for commit messages and PR titles.

### Commit Message Format

All PR titles must follow the Conventional Commits format:

```
<type>: <description>
```

or with optional scope:

```
<type>(<scope>): <description>
```

**Allowed types:**
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that do not affect the meaning of the code (white-space, formatting, etc)
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `perf`: A code change that improves performance
- `test`: Adding missing tests or correcting existing tests
- `build`: Changes that affect the build system or external dependencies
- `ci`: Changes to CI configuration files and scripts
- `chore`: Other changes that don't modify src or test files
- `revert`: Reverts a previous commit

**Examples:**
- `feat: add support for relay events`
- `fix: correct time calculation in results`
- `docs: update API documentation`
- `chore(deps): update spring boot to 3.5.7`

### Contribution Steps

1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Make your changes
4. Commit your changes using conventional commit format
5. Push to the branch (`git push origin feature-branch`)
6. Create a new Pull Request with a title following the conventional commit format

### License
This project is licensed under the MIT License - see the LICENSE file for details.