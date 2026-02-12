# Backend Project Structure

This directory contains the Spring Boot backend for the Point-of-Sale System.

## 📁 Directory Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/pos/backend/
│   │   │   ├── PointOfSaleApplication.java  # Main application entry point
│   │   │   ├── config/                      # Configuration classes
│   │   │   │   └── OpenAPIConfig.java       # Swagger/API documentation config
│   │   │   ├── domain/                      # JPA entities (database models)
│   │   │   ├── repository/                  # Database access layer
│   │   │   ├── service/                     # Business logic layer
│   │   │   ├── controller/                  # REST API endpoints
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   ├── mapper/                      # Entity ↔ DTO converters
│   │   │   ├── security/                    # Security configuration
│   │   │   │   └── SecurityConfig.java      # Spring Security setup
│   │   │   └── exception/                   # Custom exceptions & error handling
│   │   └── resources/
│   │       ├── application.properties       # Main configuration
│   │       ├── application-dev.properties   # Development profile
│   │       └── application-prod.properties  # Production profile
│   └── test/                                # Unit and integration tests
└── pom.xml                                  # Maven dependencies
```

## 🚀 Getting Started

### Prerequisites
- Java 17+ (you have Java 22 ✓)
- Maven 3.6+ (you have Maven 3.9.12 ✓)
- PostgreSQL 14+ (install later) or H2 (included)

### Running the Application

```bash
# Navigate to backend directory
cd backend

# Download dependencies and compile (first time only)
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Accessing Key URLs

- **Swagger UI (API Docs):** http://localhost:8080/swagger-ui.html
- **H2 Database Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:posdb`
  - Username: `sa`
  - Password: (leave empty)

## 📦 Dependencies Explained

- **Spring Boot Web** - REST API framework
- **Spring Data JPA** - Database operations
- **PostgreSQL Driver** - PostgreSQL connection
- **Spring Security** - Authentication & authorization
- **Lombok** - Reduces boilerplate code
- **SpringDoc OpenAPI** - Auto-generates API documentation
- **H2 Database** - In-memory database (development)

## 🔧 Development Workflow

1. **Domain Layer:** Create entity classes with JPA annotations
2. **Repository Layer:** Create repository interfaces (Spring auto-implements!)
3. **Service Layer:** Implement business logic
4. **Controller Layer:** Create REST endpoints
5. **Test:** Write unit tests for your code

## 📝 Next Steps

Week 1: ✓ Project setup complete!
Week 2: Convert domain models to JPA entities
Week 3: Create repositories and services
Week 4: Build REST API endpoints

## 🐛 Troubleshooting

**Port 8080 already in use?**
```bash
# Change port in application.properties
server.port=8081
```

**Maven dependency errors?**
```bash
# Force reload dependencies
mvn clean install -U
```

**Can't access H2 console?**
- Check `spring.h2.console.enabled=true` in application.properties
- Make sure URL is `jdbc:h2:mem:posdb`
