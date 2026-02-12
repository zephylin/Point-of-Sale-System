# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Complete Spring Boot 3.2 backend structure with Maven
- Person entity with JPA annotations and Lombok
- PersonRepository with Spring Data JPA
  - Automatic CRUD operations
  - Custom query methods (findBySsn, findByState, etc.)
- PersonService with business logic
  - Input validation (SSN format, required fields)
  - Duplicate checking
  - Data normalization (state uppercase)
- PersonController with REST endpoints
  - GET /api/persons - Get all persons
  - GET /api/persons/{id} - Get by ID
  - GET /api/persons/ssn/{ssn} - Get by SSN
  - GET /api/persons/search?name={name} - Search by name
  - POST /api/persons - Create person
  - PUT /api/persons/{id} - Update person
  - DELETE /api/persons/{id} - Delete person
  - GET /api/persons/count - Count persons
- H2 in-memory database for development
- Swagger/OpenAPI documentation at /swagger-ui.html
- H2 Console for database inspection at /h2-console
- Security configuration allowing open access for development
- Comprehensive documentation
  - MODERNIZATION_WORKPLAN.md - 7-week implementation guide
  - backend/README.md - Backend-specific documentation
  - Updated root README.md with current status

### Changed
- Project architecture from desktop (Swing) to web-based (REST API)
- Data storage from CSV files to relational database
- Security configuration to permit all requests during development

### Technical Details
- **Java Version**: 21
- **Spring Boot Version**: 3.2.2
- **Database**: H2 (in-memory) for development
- **Build Tool**: Maven 3.9+
- **Dependencies**: 
  - Spring Web (REST API)
  - Spring Data JPA (Database)
  - Spring Security (Authentication)
  - SpringDoc OpenAPI (API Documentation)
  - Lombok (Code Generation)
  - H2 Database (Development)
  - PostgreSQL Driver (Production-ready)

## [0.0.1] - 2026-02-12

### Added
- Initial Spring Boot project setup
- Maven project structure
- Configuration files (application.properties with profiles)
- Package structure (domain, repository, service, controller, dto, mapper, config, security, exception)
- OpenAPIConfig for Swagger documentation
- SecurityConfig for Spring Security
- .gitignore files for clean repository

### Infrastructure
- Development environment verified (Java 22, Maven 3.9.12)
- Application successfully builds and runs
- Port 8080 configured and accessible
- Logging configured for development

---

## Notes

### Testing Performed
- ✅ Maven build successful
- ✅ Spring Boot application starts successfully
- ✅ H2 database auto-creates tables from entities
- ✅ Swagger UI accessible and functional
- ✅ H2 Console accessible
- ✅ All Person API endpoints tested and working
- ✅ Person creation, retrieval, and validation working correctly
- ✅ Security configuration allows API access

### Known Issues
- None at this time

### Next Steps
1. Create remaining domain entities (Store, Item, TaxCategory, Sale, etc.)
2. Implement entity relationships (@OneToMany, @ManyToOne, etc.)
3. Add more complex business logic
4. Implement proper JWT authentication
5. Add comprehensive unit and integration tests
6. Set up PostgreSQL for production
7. Begin React frontend development
