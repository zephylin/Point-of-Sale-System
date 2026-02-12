package com.pos.backend.repository;

import com.pos.backend.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Person Repository Interface
 * 
 * ============================================
 * WHAT IS A REPOSITORY?
 * ============================================
 * A Repository is the data access layer - it talks to the database.
 * 
 * Think of it as a "storage manager" for Person entities.
 * It handles all CRUD operations (Create, Read, Update, Delete).
 * 
 * ============================================
 * THE MAGIC OF SPRING DATA JPA
 * ============================================
 * You write an INTERFACE (not a class!), and Spring automatically:
 * 1. Creates the implementation class for you
 * 2. Generates all the SQL queries
 * 3. Handles database connections
 * 4. Manages transactions
 * 
 * You get all this WITHOUT writing a single line of SQL!
 * 
 * ============================================
 * HOW TO USE THIS
 * ============================================
 * In your service/controller, inject this repository:
 * 
 * @Autowired
 * private PersonRepository personRepository;
 * 
 * Then use it:
 * Person person = personRepository.findById(1L).orElse(null);
 * personRepository.save(person);
 * personRepository.delete(person);
 * 
 * @author Your Name
 * @version 1.0
 */
@Repository  // Marks this as a Spring Data repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    /*
     * ============================================
     * INHERITED METHODS (Provided by JpaRepository)
     * ============================================
     * You get these methods for FREE:
     * 
     * save(Person person)              - Insert or update person
     * findById(Long id)                - Find person by ID  
     * findAll()                        - Get all persons
     * findAll(Pageable pageable)       - Get persons with pagination
     * count()                          - Count total persons
     * delete(Person person)            - Delete person
     * deleteById(Long id)              - Delete person by ID
     * existsById(Long id)              - Check if person exists
     * 
     * And many more!
     */
    
    
    /*
     * ============================================
     * CUSTOM QUERY METHODS
     * ============================================
     * Spring Data JPA creates queries based on method names!
     * 
     * Method naming convention:
     * findBy + FieldName + Condition
     * 
     * Examples:
     * - findByFirstName      → WHERE first_name = ?
     * - findByLastName      → WHERE last_name = ?
     * - findByFirstNameAndLastName → WHERE first_name = ? AND last_name = ?
     * - findByStateIn       → WHERE state IN (?)
     * - findByFirstNameContaining → WHERE first_name LIKE %?%
     */
    
    /**
     * Find person by SSN (Social Security Number)
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE ssn = ?
     * 
     * @param ssn Social Security Number
     * @return Optional<Person> - Contains person if found, empty if not found
     * 
     * WHY Optional?
     * Optional prevents NullPointerException. You check if value exists:
     * 
     * Optional<Person> result = personRepository.findBySsn("123-45-6789");
     * if (result.isPresent()) {
     *     Person person = result.get();
     * } else {
     *     // Handle not found
     * }
     */
    Optional<Person> findBySsn(String ssn);
    
    /**
     * Find persons by first name
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE first_name = ?
     * 
     * @param firstName First name to search for
     * @return List of persons with matching first name
     */
    List<Person> findByFirstName(String firstName);
    
    /**
     * Find persons by last name
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE last_name = ?
     * 
     * @param lastName Last name to search for
     * @return List of persons with matching last name
     */
    List<Person> findByLastName(String lastName);
    
    /**
     * Find persons by state
     * 
     * Useful for filtering persons by location
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE state = ?
     * 
     * @param state State code (e.g., "CA", "NY")
     * @return List of persons in that state
     */
    List<Person> findByState(String state);
    
    /**
     * Find persons by city
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE city = ?
     * 
     * @param city City name
     * @return List of persons in that city
     */
    List<Person> findByCity(String city);
    
    /**
     * Find persons by first name OR last name (case insensitive)
     * 
     * Great for search functionality!
     * 
     * Generated SQL:
     * SELECT * FROM persons 
     * WHERE LOWER(first_name) = LOWER(?) OR LOWER(last_name) = LOWER(?)
     * 
     * @param firstName First name to search
     * @param lastName Last name to search
     * @return List of matching persons
     */
    List<Person> findByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    
    /**
     * Find persons by phone number
     * 
     * Generated SQL:
     * SELECT * FROM persons WHERE phone = ?
     * 
     * @param phone Phone number
     * @return Optional<Person>
     */
    Optional<Person> findByPhone(String phone);
    
    /**
     * Check if person with SSN exists
     * 
     * Useful for validation before creating new person
     * 
     * Generated SQL:
     * SELECT EXISTS(SELECT 1 FROM persons WHERE ssn = ?)
     * 
     * @param ssn Social Security Number
     * @return true if exists, false otherwise
     */
    boolean existsBySsn(String ssn);
    
    /*
     * ============================================
     * ADVANCED: CUSTOM QUERIES WITH @Query
     * ============================================
     * For complex queries, you can write your own JPQL/SQL
     * 
     * Example (uncomment if needed):
     * 
     * @Query("SELECT p FROM Person p WHERE p.city = ?1 AND p.state = ?2")
     * List<Person> findByCityAndState(String city, String state);
     * 
     * @Query(value = "SELECT * FROM persons WHERE LENGTH(phone) = 10",
     *        nativeQuery = true)
     * List<Person> findPersonsWithValidPhoneNumbers();
     */
}
