# stockapp

[![Java CI with Maven and Testcontainers](https://github.com/kakiang/stockapp/actions/workflows/maven.yml/badge.svg)](https://github.com/kakiang/stockapp/actions/workflows/maven.yml)

A demo project for managing stock data, built with Spring Boot.

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kakiang/stockapp.git
   cd stockapp
   ```

2. **Configure Database:**

   - For PostgreSQL:
     Edit `src/main/resources/application.properties` to set your PostgreSQL credentials.
     No configuration needed.

3. **Build and Run:**
   ```bash
   ./mvnw spring-boot:run
   ```

   Or to build:
   ```bash
   ./mvnw clean package
   java -jar target/stockapp-0.0.1-SNAPSHOT.jar
   ```

## Project Structure

- `src/main/java` - Application source code.
- `src/main/resources` - Configuration files (e.g., `application.properties`).
- `src/test/java` - Test classes.
- `pom.xml` - Maven dependencies and plugins.

## Dependencies

- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-data-jpa` - JPA and database support
- `postgresql` - PostgreSQL driver (runtime)
- `lombok` - Reduces boilerplate code
- `spring-boot-devtools` - Hot reloading for development
- `spring-boot-starter-test` - Testing support
- `spring-boot-testcontainers` -  Testing support (lightweight services in containers)

## API Endpoints
> - `GET http://localhost:8080/api/categories`
> - `GET http://localhost:8080/api/categories/all`
> - `GET http://localhost:8080/api/categories/{id}`
> - `POST http://localhost:8080/api/categories`
> - `PUT http://localhost:8080/api/categories/{id}`
> - `DELETE http://localhost:8080/api/categories/{id}`
> - `GET http://localhost:8080/api/produits`

## License

This project is for demo purposes.
