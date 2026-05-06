# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Frontend Testing Framework** with Vitest and React Testing Library (49 tests)
  - Vitest configured with jsdom environment, global test utilities, and CSS support
  - Test setup with `@testing-library/jest-dom` matchers
  - Component tests: `Alert.test.jsx` (7), `Modal.test.jsx` (8), `ConfirmDialog.test.jsx` (7)
  - Page integration tests: `Login.test.jsx` (8), `Dashboard.test.jsx` (8), `Reports.test.jsx` (12)
  - Tests cover rendering, user interactions, API mocking, error states, navigation, and accessibility
  - CI pipeline updated to run `npm run test:run` before build
  - DevDependencies: vitest, @testing-library/react, @testing-library/jest-dom, @testing-library/user-event, jsdom
- **JWT Authentication & Authorization** — Full-stack secure login system
  - `JwtService.java` — Token generation and validation using JJWT 0.12.5 (HS256, configurable secret and expiration)
  - `JwtAuthenticationFilter.java` — Extracts Bearer token from Authorization header, sets Spring Security context
  - `CustomUserDetailsService.java` — Loads cashier by number from database, maps role to Spring Security authority
  - `AuthController.java` — POST `/api/auth/login` endpoint with cashier number/password authentication
  - `AuthDTO.java` — LoginRequest (with validation) and LoginResponse (token, cashierNumber, name, role)
  - Updated `SecurityConfig.java` — Stateless sessions, JWT filter chain, permits auth/swagger/h2-console, protects all other endpoints
  - Updated `DataSeeder.java` — Passwords now BCrypt-hashed via PasswordEncoder
  - Updated `CashierService.java` — Encodes passwords on create and update
  - Updated `application.properties` — JWT secret and expiration configuration (env-var overridable)
  - Added JJWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.5) to `pom.xml`
  - `Login.jsx` — Login page with cashier number/password form, error handling, demo account hints
  - Updated `App.jsx` — Protected routes with auth guard, login/logout state management
  - Updated `Layout.jsx` — Sidebar shows logged-in user name, role, and logout button
  - Updated `client.js` — Request interceptor adds Bearer token, 401 response redirects to login
  - Login page CSS in `index.css` — Gradient background, card layout, responsive design
  - Updated all 11 controller tests with security mock beans (`JwtService`, `UserDetailsService`)
  - Updated `CashierServiceTest` with `PasswordEncoder` mock
  - All 427 tests passing
- **Docker containerization** for full-stack deployment
  - `backend/Dockerfile` — Multi-stage build (Maven 3.9 + JDK 21 build, JRE 21 runtime), runs as non-root user
  - `frontend/Dockerfile` — Multi-stage build (Node 20 build, nginx Alpine runtime)
  - `frontend/nginx.conf` — Nginx reverse proxy config that routes `/api/` and `/swagger-ui/` to backend, serves React SPA with client-side routing
  - `docker-compose.yml` — Orchestrates 3 services: PostgreSQL 16, Spring Boot backend, React frontend
  - `application-docker.properties` — Spring profile for Docker environment (PostgreSQL dialect, env-var datasource)
  - `.dockerignore` files for backend and frontend
  - PostgreSQL health check with `pg_isready` before backend starts
  - Named volume `postgres_data` for persistent database storage
- **CI/CD Pipeline** with GitHub Actions (`.github/workflows/ci.yml`)
  - Runs automatically on push to `main` and on pull requests
  - **Backend job**: Checks out code, sets up JDK 21 (Temurin), caches Maven dependencies, runs `mvn test` (427 tests)
  - **Frontend job**: Checks out code, sets up Node.js 20, caches npm, runs `npm ci` and `npm run build`
  - Uploads Surefire test reports as artifacts (7-day retention)
  - Jobs run in parallel for faster feedback
- CI status badge added to README.md
- README.md updated with accurate project status and technology stack
- **Controller integration tests** (`@WebMvcTest`) for all 11 REST controllers (134 new tests, 427 total)
  - `StoreControllerTest.java` — GET all/byId/search, POST create with validation, PUT update, DELETE, PATCH deactivate, GET count
  - `ItemControllerTest.java` — GET all/byId/search/barcode/active, POST create with validation, PUT update, DELETE, GET count
  - `PersonControllerTest.java` — GET all/byId/search, POST create with validation (name, SSN format), PUT update, DELETE, GET count
  - `CashierControllerTest.java` — GET all/byId/byNumber/byStore/active, POST createWithIds with validation, PUT update, PATCH terminate, DELETE, GET count
  - `RegisterControllerTest.java` — GET all/byId/byNumber/byStore/active, POST createWithIds with validation, PUT update, PATCH updateStatus, DELETE, GET count
  - `TaxCategoryControllerTest.java` — GET all/byId/byName/search/active, POST create with validation, PUT update, PATCH deactivate, DELETE, GET count
  - `TaxRateControllerTest.java` — GET all/byId/byCategory/currentRate, POST create with validation (rate bounds), PUT update, DELETE, GET count
  - `SessionControllerTest.java` — GET all/byId/byCashier/activeByCashier, POST createWithIds with validation, PATCH close, DELETE, GET count
  - `SaleControllerTest.java` — GET all/byId/bySession/totalByStore, POST createWithIds with validation, PATCH complete/void, DELETE, GET count
  - `SaleLineItemControllerTest.java` — GET all/byId/bySale/totalQuantity, POST createWithIds with validation (quantity min), PUT update, DELETE, GET count
  - `PaymentControllerTest.java` — GET all/byId/bySale/byType/totalBySale, POST create with validation (paymentType regex, amount positive), PUT update with 404, DELETE, GET count
  - Uses `@AutoConfigureMockMvc(addFilters = false)` to bypass Spring Security in tests
  - Tests verify HTTP status codes (200, 201, 204, 400, 404), JSON response paths, and Jakarta validation
- **DataSeeder** (`config/DataSeeder.java`) — seeds the H2 database with sample data on startup
  - 1 store (David's Quick Mart) with address and contact info
  - 3 persons (David, Sally, Mike) with full address details
  - 3 cashiers with roles (Supervisor, Cashier) linked to persons and store
  - 4 tax categories (Food 7%, Beverage 0%, General Merchandise 8.5%, Alcohol 13%) with rates
  - 10 items (sandwiches, drinks, snacks, office supplies) with prices, stock levels, barcodes, and SKUs
  - 3 registers linked to the store
  - Only runs on non-prod profiles; skips seeding if data already exists
  - Data inspired by the original `StoreData.csv`
- **Unit tests** for all remaining service classes (6 new test files, 293 total tests pass)
  - `CashierServiceTest.java` — CRUD, createWithIds, terminate, validation
  - `RegisterServiceTest.java` — CRUD, createWithIds, updateStatus, validation
  - `TaxCategoryServiceTest.java` — CRUD, search, deactivate, validation
  - `TaxRateServiceTest.java` — CRUD, createWithIds, rate lookups, validation
  - `PaymentServiceTest.java` — CASH/CREDIT/CHECK creation, validation, aggregations
  - `SaleLineItemServiceTest.java` — CRUD, createWithIds, price calculations, validation
- **Payment entity** with full stack support for CASH, CREDIT, and CHECK payment types
  - `Payment.java` domain entity with single-table design (payment_type discriminator)
  - `PaymentRepository.java` with queries by sale, type, date range, and aggregations
  - `PaymentService.java` with business logic, type-specific validation, and auto-authorization
  - `PaymentDTO.java` with Request/Response inner classes and Jakarta validation
  - `PaymentMapper.java` for entity ↔ DTO conversion
  - `PaymentController.java` with full REST API:
    - GET /api/payments - Get all payments
    - GET /api/payments/{id} - Get by ID
    - GET /api/payments/sale/{saleId} - Get payments for a sale
    - GET /api/payments/type/{paymentType} - Get by payment type
    - GET /api/payments/date-range - Get by date range
    - GET /api/payments/total/sale/{saleId} - Total payments for a sale
    - GET /api/payments/total/cash/session/{sessionId} - Total cash by session
    - POST /api/payments - Create payment
    - PUT /api/payments/{id} - Update payment
    - DELETE /api/payments/{id} - Delete payment
    - GET /api/payments/count - Count all payments
    - GET /api/payments/count/sale/{saleId} - Count by sale
- Payment relationship added to Sale entity (`@OneToMany` payments list)
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
