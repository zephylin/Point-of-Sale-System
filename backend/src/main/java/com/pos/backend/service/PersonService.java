package com.pos.backend.service;

import com.pos.backend.domain.Person;
import com.pos.backend.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Person Service Layer
 * 
 * ============================================
 * WHAT IS A SERVICE LAYER?
 * ============================================
 * The service layer contains BUSINESS LOGIC.
 * 
 * Architecture flow:
 * Controller → Service → Repository → Database
 * 
 * CONTROLLER: Handles HTTP requests/responses
 * SERVICE: Business logic, validation, orchestration
 * REPOSITORY: Database operations
 * 
 * ============================================
 * WHY SEPARATE SERVICE FROM CONTROLLER?
 * ============================================
 * 1. REUSABILITY: Multiple controllers can use same service
 * 2. TESTABILITY: Easy to test business logic separately
 * 3. TRANSACTIONS: Service manages database transactions
 * 4. BUSINESS RULES: Complex logic stays out of controllers
 * 
 * Example: When creating a Person, we might need to:
 * - Validate SSN format
 * - Check for duplicates
 * - Send welcomeEmail
 * - Log the creation
 * 
 * All this belongs in Service, not Controller!
 * 
 * ============================================
 * ANNOTATIONS EXPLAINED
 * ============================================
 * @Service - Marks this as a Spring service component
 * @RequiredArgsConstructor - Lombok creates constructor with final fields
 * @Slf4j - Lombok adds logger: log.info(), log.error(), etc.
 * @Transactional - Wraps method in database transaction
 * 
 * @author Your Name
 * @version 1.0
 */
@Service  // Spring: This is a service component
@RequiredArgsConstructor  // Lombok: Creates constructor for final fields
@Slf4j  // Lombok: Adds logger (log.info, log.error, etc.)
public class PersonService {

    /**
     * Repository Dependency
     * 
     * This is DEPENDENCY INJECTION:
     * - Declared as 'final'
     * - @RequiredArgsConstructor creates constructor
     * - Spring automatically injects PersonRepository
     * 
     * No need for @Autowired annotation!
     */
    private final PersonRepository personRepository;

    /**
     * Get all persons
     * 
     * @return List of all persons in database
     */
    public List<Person> getAllPersons() {
        log.info("Fetching all persons from database");
        return personRepository.findAll();
    }

    /**
     * Get person by ID
     * 
     * @param id Person ID
     * @return Person object
     * @throws RuntimeException if person not found
     * 
     * TIP: In Week 4, we'll create custom exceptions like PersonNotFoundException
     */
    public Person getPersonById(Long id) {
        log.info("Fetching person with ID: {}", id);
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
    }

    /**
     * Get person by SSN
     * 
     * @param ssn Social Security Number
     * @return Person object
     * @throws RuntimeException if person not found
     */
    public Person getPersonBySsn(String ssn) {
        log.info("Fetching person with SSN: {}", maskSsn(ssn));  // Security: mask SSN in logs
        return personRepository.findBySsn(ssn)
                .orElseThrow(() -> new RuntimeException("Person not found with SSN: " + maskSsn(ssn)));
    }

    /**
     * Search persons by name
     * 
     * Searches by first name OR last name (case insensitive)
     * 
     * @param name Name to search for
     * @return List of matching persons
     */
    public List<Person> searchPersonsByName(String name) {
        log.info("Searching persons by name: {}", name);
        return personRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(name, name);
    }

    /**
     * Get persons by state
     * 
     * @param state State code (e.g., "CA", "NY")
     * @return List of persons in that state
     */
    public List<Person> getPersonsByState(String state) {
        log.info("Fetching persons in state: {}", state);
        return personRepository.findByState(state);
    }

    /**
     * Create new person
     * 
     * @Transactional ensures:
     * - If any error occurs, database changes are rolled back
     * - All operations succeed or all fail (atomicity)
     * 
     * Business logic:
     * 1. Validate SSN format
     * 2. Check if SSN already exists
     * 3. Sanitize input data
     * 4. Save to database
     * 
     * @param person Person object to create
     * @return Saved person (with generated ID)
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Person createPerson(Person person) {
        log.info("Creating new person: {} {}", person.getFirstName(), person.getLastName());

        // Business Rule 1: Validate required fields
        validatePerson(person);

        // Business Rule 2: Check for duplicate SSN
        if (person.getSsn() != null && personRepository.existsBySsn(person.getSsn())) {
            log.error("Person with SSN {} already exists", maskSsn(person.getSsn()));
            throw new IllegalArgumentException("Person with this SSN already exists");
        }

        // Business Rule 3: Sanitize/normalize data
        person.setState(person.getState().toUpperCase());  // Normalize state to uppercase

        // Save to database
        Person savedPerson = personRepository.save(person);
        log.info("Successfully created person with ID: {}", savedPerson.getId());

        return savedPerson;
    }

    /**
     * Update existing person
     * 
     * @Transactional ensures atomicity
     * 
     * @param id Person ID to update
     * @param personDetails New person data
     * @return Updated person
     * @throws RuntimeException if person not found
     */
    @Transactional
    public Person updatePerson(Long id, Person personDetails) {
        log.info("Updating person with ID: {}", id);

        // Fetch existing person
        Person existingPerson = getPersonById(id);

        // Update fields
        existingPerson.setFirstName(personDetails.getFirstName());
        existingPerson.setLastName(personDetails.getLastName());
        existingPerson.setAddress(personDetails.getAddress());
        existingPerson.setCity(personDetails.getCity());
        existingPerson.setState(personDetails.getState().toUpperCase());
        existingPerson.setZip(personDetails.getZip());
        existingPerson.setPhone(personDetails.getPhone());
        
        // Note: SSN should NOT be updatable for security reasons
        // If allowing SSN update, check for duplicates first

        // Save and return
        Person updatedPerson = personRepository.save(existingPerson);
        log.info("Successfully updated person with ID: {}", id);

        return updatedPerson;
    }

    /**
     * Delete person by ID
     * 
     * @Transactional ensures rollback if error occurs
     * 
     * @param id Person ID to delete
     * @throws RuntimeException if person not found
     */
    @Transactional
    public void deletePerson(Long id) {
        log.info("Deleting person with ID: {}", id);

        // Check if person exists
        Person person = getPersonById(id);

        // Business Rule: Check if person is associated with a cashier
        // If yes, might want to prevent deletion or handle cascading
        // For now, we'll allow deletion

        personRepository.delete(person);
        log.info("Successfully deleted person with ID: {}", id);
    }

    /**
     * Count total persons
     * 
     * @return Total number of persons in database
     */
    public long countPersons() {
        long count = personRepository.count();
        log.info("Total persons in database: {}", count);
        return count;
    }

    /**
     * Check if person exists by ID
     * 
     * @param id Person ID
     * @return true if exists, false otherwise
     */
    public boolean personExists(Long id) {
        return personRepository.existsById(id);
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    /**
     * Validate person data
     * 
     * This is where business rules go!
     * 
     * @param person Person to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePerson(Person person) {
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        // Validate SSN format: "123-45-6789"
        if (person.getSsn() != null && !person.getSsn().matches("\\d{3}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException("Invalid SSN format. Expected: 123-45-6789");
        }

        // Validate state (should be 2 letter code)
        if (person.getState() != null && person.getState().length() != 2) {
            throw new IllegalArgumentException("State must be 2 letter code (e.g., CA, NY)");
        }

        // Add more validations as needed:
        // - Phone number format
        // - ZIP code format
        // - Email format (if you add email field)
    }

    /**
     * Mask SSN for logging
     * 
     * SECURITY BEST PRACTICE:
     * Never log full SSN! Mask it for privacy.
     * 
     * Example: "123-45-6789" → "***-**-6789"
     * 
     * @param ssn Social Security Number
     * @return Masked SSN
     */
    private String maskSsn(String ssn) {
        if (ssn == null || ssn.length() < 4) {
            return "***";
        }
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
}
