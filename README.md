# Point-of-Sale-System 🛒 🚀

[![CI Pipeline](https://github.com/zephylin/Point-of-Sale-System/actions/workflows/ci.yml/badge.svg)](https://github.com/zephylin/Point-of-Sale-System/actions/workflows/ci.yml)

## Overview 📋
A modern, full-stack Point of Sale system currently undergoing modernization from a legacy Java Swing application to a contemporary web-based architecture using Spring Boot and React.

The original system was a Java desktop application designed to manage retail operations including sales, inventory, cashier management, and payment processing.

## 🎯 Modernization Status

**Current Phase:** Testing & CI/CD (Week 4-5 of 7-week plan)

### ✅ Completed
- [x] Spring Boot 3.2 backend with 11 domain entities and full JPA relationships
- [x] H2 in-memory database with sample data seeder
- [x] Repository layer with Spring Data JPA (11 repositories with custom queries)
- [x] Service layer with business logic & validation (11 services)
- [x] REST API with 11 controllers and comprehensive endpoints
- [x] DTOs, Mappers, and Jakarta Bean Validation
- [x] Global exception handling with custom error responses
- [x] Swagger/OpenAPI documentation
- [x] Security configuration (Spring Security, BCrypt)
- [x] React frontend with 8 pages (Dashboard, POS Terminal, CRUD management)
- [x] Unit tests for all 11 service classes (293 tests)
- [x] Controller integration tests for all 11 REST controllers (134 tests)
- [x] **427 total tests passing**
- [x] CI/CD pipeline with GitHub Actions

### 📋 Planned
- [ ] JWT authentication & authorization
- [ ] Docker containerization
- [ ] PostgreSQL integration for production
- [ ] Cloud deployment

## 🏗️ Architecture

### Legacy System (src/)
- **POS_PD**: Domain models (Store, Sale, Item, Cashier, etc.)
- **POS_DM**: CSV-based data management
- **POS_UI**: Java Swing user interface
- **POS_Tests**: Acceptance criteria tests

### Modern Backend (backend/)
```
backend/
├── src/main/java/com/pos/backend/
│   ├── domain/           # JPA entities
│   ├── repository/       # Spring Data repositories
│   ├── service/          # Business logic
│   ├── controller/       # REST API endpoints
│   ├── dto/              # Data Transfer Objects
│   ├── mapper/           # Entity-DTO converters
│   ├── config/           # Spring configuration
│   └── security/         # Security configuration
└── src/main/resources/
    └── application.properties
```

## 🚀 Getting Started

### Prerequisites
- Java 17+ (Java 21 recommended)
- Maven 3.6+
- (Optional) PostgreSQL 14+ for production

### Running the Backend

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
java -jar target/point-of-sale-backend-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Accessing the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:posdb`
  - Username: `sa`
  - Password: (empty)

### Example API Calls

```bash
# Get all persons
curl http://localhost:8080/api/persons

# Create a person
curl -X POST http://localhost:8080/api/persons \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "city": "San Francisco",
    "state": "CA",
    "ssn": "123-45-6789"
  }'

# Get person by ID
curl http://localhost:8080/api/persons/1
```

## 📚 Documentation

- **[Modernization Work Plan](MODERNIZATION_WORKPLAN.md)** - Comprehensive 7-week implementation guide
- **Project Structure** - See backend/README.md
- **API Documentation** - Available at Swagger UI when application is running

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2
- **Language**: Java 21
- **Database**: H2 (development), PostgreSQL (planned for production)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ, MockMvc (427 tests)

### Frontend
- **Framework**: React 19
- **UI Library**: Lucide React Icons
- **Routing**: React Router 7
- **HTTP Client**: Axios
- **Build Tool**: Vite 6

### DevOps
- **CI/CD**: GitHub Actions (automated tests on push/PR)
- **Containerization**: Docker (planned)
- **Deployment**: Render.com / Vercel (planned)

## 📊 Legacy Features

The original system supports:
- ✅ Sales Management with tax calculations
- ✅ Multi-Payment Support (Cash, Credit, Check)
- ✅ Tax Categories and flexible rate application
- ✅ Promotional pricing
- ✅ Cashier authentication and session management
- ✅ Inventory management
- ✅ Sales reporting

These features are being modernized with improved architecture, security, and user experience.

## 🤝 Contributing

This project is currently under active development as part of a portfolio modernization effort. Contributions, suggestions, and feedback are welcome!

## 📝 License

See LICENSE file for details.

## 👤 Author

Portfolio project for entry-level Software Engineering positions - demonstrating full-stack development capabilities, modern architecture patterns, and best practices.

---

**Note**: This is an active modernization project. The `src/` directory contains the legacy application, while `backend/` contains the new Spring Boot implementation. Frontend development will begin after backend completion.
