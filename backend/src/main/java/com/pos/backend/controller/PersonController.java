package com.pos.backend.controller;

import com.pos.backend.domain.Person;
import com.pos.backend.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Person REST Controller
 * 
 * ==================================================================
 * WHAT IS A REST CONTROLLER?
 * ==================================================================
 * A Controller handles HTTP requests and returns HTTP responses.
 * 
 * HTTP Request → Controller → Service → Repository → Database
 *                     ↓
 *              HTTP Response (JSON)
 * 
 * REST API means:
 * - URLs represent resources: /api/persons
 * - HTTP methods indicate actions:
 *   GET    = Read/Retrieve
 *   POST   = Create
 *   PUT    = Update
 *   DELETE = Delete
 * 
 * ==================================================================
 * ANNOTATIONS EXPLAINED
 * ==================================================================
 * @RestController = @Controller + @ResponseBody
 * - Handles HTTP requests
 * - Automatically converts return values to JSON
 * 
 * @RequestMapping("/api/persons")
 * - Base URL for all endpoints in this controller
 * - Example: /api/persons, /api/persons/1, etc.
 * 
 * @CrossOrigin
 * - Allows frontend (React) running on different port to call this API
 * - Development: Frontend on :3000, Backend on :8080
 * 
 * @RequiredArgsConstructor
 * - Lombok: Creates constructor for final fields (dependency injection)
 * 
 * @Slf4j
 * - Lombok: Adds logger
 * 
 * ==================================================================
 * HTTP STATUS CODES USED
 * ==================================================================
 * 200 OK              - Success (GET, PUT)
 * 201 CREATED         - Resource created successfully (POST)
 * 204 NO CONTENT      - Success with no response body (DELETE)
 * 400 BAD REQUEST     - Invalid input
 * 404 NOT FOUND       - Resource doesn't exist
 * 500 INTERNAL ERROR  - Server error
 * 
 * @author Your Name
 * @version 1.0
 */
@RestController                                    // Marks this as REST API controller
@RequestMapping("/api/persons")                   // Base URL: /api/persons
@CrossOrigin(origins = "http://localhost:3000")   // Allow React frontend to call this
@RequiredArgsConstructor                          // Lombok: Constructor injection
@Slf4j                                            // Lombok: Logger
@Tag(name = "Person Management", description = "APIs for managing persons in the POS system")  // Swagger documentation
public class PersonController {

    /**
     * Inject PersonService
     * 
     * Spring automatically injects this when controller is created
     */
    private final PersonService personService;

    /**
     * GET /api/persons
     * Get all persons
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons
     * 
     * EXAMPLE RESPONSE:
     * [
     *   {
     *     "id": 1,
     *     "firstName": "John",
     *     "lastName": "Doe",
     *     "city": "San Francisco",
     *     "state": "CA",
     *     ...
     *   },
     *   ...
     * ]
     * 
     * @return List of all persons (200 OK)
     */
    @GetMapping
    @Operation(summary = "Get all persons", description = "Retrieves a list of all persons in the system")
    public ResponseEntity<List<Person>> getAllPersons() {
        log.info("REST: GET /api/persons - Getting all persons");
        List<Person> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);  // 200 OK
    }

    /**
     * GET /api/persons/{id}
     * Get person by ID
     * 
     * @PathVariable extracts {id} from URL
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/1
     * 
     * EXAMPLE RESPONSE:
     * {
     *   "id": 1,
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   ...
     * }
     * 
     * @param id Person ID from URL
     * @return Person object (200 OK) or error (404 NOT FOUND)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID", description = "Retrieves a specific person by their ID")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        log.info("REST: GET /api/persons/{} - Getting person by ID", id);
        Person person = personService.getPersonById(id);
        return ResponseEntity.ok(person);  // 200 OK
    }

    /**
     * GET /api/persons/ssn/{ssn}
     * Get person by SSN
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/ssn/123-45-6789
     * 
     * @param ssn Social Security Number from URL
     * @return Person object (200 OK) or error (404 NOT FOUND)
     */
    @GetMapping("/ssn/{ssn}")
    @Operation(summary = "Get person by SSN", description = "Retrieves a person by their Social Security Number")
    public ResponseEntity<Person> getPersonBySsn(@PathVariable String ssn) {
        log.info("REST: GET /api/persons/ssn/{} - Getting person by SSN", ssn);
        Person person = personService.getPersonBySsn(ssn);
        return ResponseEntity.ok(person);  // 200 OK
    }

    /**
     * GET /api/persons/search?name={name}
     * Search persons by name
     * 
     * @RequestParam extracts query parameter from URL
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/search?name=John
     * 
     * @param name Name to search (searches both first and last name)
     * @return List of matching persons (200 OK)
     */
    @GetMapping("/search")
    @Operation(summary = "Search persons by name", description = "Searches persons by first or last name (case insensitive)")
    public ResponseEntity<List<Person>> searchPersons(@RequestParam String name) {
        log.info("REST: GET /api/persons/search?name={} - Searching persons", name);
        List<Person> persons = personService.searchPersonsByName(name);
        return ResponseEntity.ok(persons);  // 200 OK
    }

    /**
     * GET /api/persons/state/{state}
     * Get persons by state
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/state/CA
     * 
     * @param state State code (e.g., "CA", "NY")
     * @return List of persons in that state (200 OK)
     */
    @GetMapping("/state/{state}")
    @Operation(summary = "Get persons by state", description = "Retrieves all persons in a specific state")
    public ResponseEntity<List<Person>> getPersonsByState(@PathVariable String state) {
        log.info("REST: GET /api/persons/state/{} - Getting persons by state", state);
        List<Person> persons = personService.getPersonsByState(state);
        return ResponseEntity.ok(persons);  // 200 OK
    }

    /**
     * POST /api/persons
     * Create new person
     * 
     * @RequestBody converts JSON from request body to Person object
     * 
     * EXAMPLE REQUEST:
     * POST http://localhost:8080/api/persons
     * Content-Type: application/json
     * 
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "address": "123 Main St",
     *   "city": "San Francisco",
     *   "state": "CA",
     *   "zip": "94102",
     *   "phone": "555-1234",
     *   "ssn": "123-45-6789"
     * }
     * 
     * EXAMPLE RESPONSE:
     * {
     *   "id": 1,  ← Database generated this
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   ...
     * }
     * 
     * @param person Person data from JSON request body
     * @return Created person with ID (201 CREATED) or error (400 BAD REQUEST)
     */
    @PostMapping
    @Operation(summary = "Create new person", description = "Creates a new person in the system")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        log.info("REST: POST /api/persons - Creating new person: {} {}",
                person.getFirstName(), person.getLastName());
        
        try {
            Person createdPerson = personService.createPerson(person);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);  // 201 CREATED
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();  // 400 BAD REQUEST
        }
    }

    /**
     * PUT /api/persons/{id}
     * Update existing person
     * 
     * Receives person ID in URL and new data in request body
     * 
     * EXAMPLE REQUEST:
     * PUT http://localhost:8080/api/persons/1
     * Content-Type: application/json
     * 
     * {
     *   "firstName": "Jane",
     *   "lastName": "Smith",
     *   ...
     * }
     * 
     * @param id Person ID to update
     * @param person New person data
     * @return Updated person (200 OK) or error (404 NOT FOUND)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update person", description = "Updates an existing person's information")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        log.info("REST: PUT /api/persons/{} - Updating person", id);
        
        try {
            Person updatedPerson = personService.updatePerson(id, person);
            return ResponseEntity.ok(updatedPerson);  // 200 OK
        } catch (RuntimeException e) {
            log.error("Error updating person: {}", e.getMessage());
            return ResponseEntity.notFound().build();  // 404 NOT FOUND
        }
    }

    /**
     * DELETE /api/persons/{id}
     * Delete person
     * 
     * EXAMPLE REQUEST:
     * DELETE http://localhost:8080/api/persons/1
     * 
     * EXAMPLE RESPONSE:
     * (No content - just HTTP 204 status)
     * 
     * @param id Person ID to delete
     * @return No content (204 NO CONTENT) or error (404 NOT FOUND)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete person", description = "Deletes a person from the system")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        log.info("REST: DELETE /api/persons/{} - Deleting person", id);
        
        try {
            personService.deletePerson(id);
            return ResponseEntity.noContent().build();  // 204 NO CONTENT
        } catch (RuntimeException e) {
            log.error("Error deleting person: {}", e.getMessage());
            return ResponseEntity.notFound().build();  // 404 NOT FOUND
        }
    }

    /**
     * GET /api/persons/count
     * Get total number of persons
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/count
     * 
     * EXAMPLE RESPONSE:
     * 42
     * 
     * @return Count of persons (200 OK)
     */
    @GetMapping("/count")
    @Operation(summary = "Count persons", description = "Returns the total number of persons in the system")
    public ResponseEntity<Long> countPersons() {
        log.info("REST: GET /api/persons/count - Counting persons");
        long count = personService.countPersons();
        return ResponseEntity.ok(count);  // 200 OK
    }

    /**
     * GET /api/persons/{id}/exists
     * Check if person exists
     * 
     * EXAMPLE REQUEST:
     * GET http://localhost:8080/api/persons/1/exists
     * 
     * EXAMPLE RESPONSE:
     * true
     * 
     * @param id Person ID
     * @return Boolean indicating existence (200 OK)
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if person exists", description = "Checks whether a person with the given ID exists")
    public ResponseEntity<Boolean> personExists(@PathVariable Long id) {
        log.info("REST: GET /api/persons/{}/exists - Checking if person exists", id);
        boolean exists = personService.personExists(id);
        return ResponseEntity.ok(exists);  // 200 OK
    }
}
