package com.pos.backend.repository;

import com.pos.backend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Store entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    /**
     * Find store by store number
     * @param number Store number
     * @return Optional containing the store if found
     */
    Optional<Store> findByNumber(String number);
    
    /**
     * Find stores by name (case-insensitive)
     * @param name Store name
     * @return List of stores with matching name
     */
    List<Store> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find stores by city
     * @param city City name
     * @return List of stores in the city
     */
    List<Store> findByCity(String city);
    
    /**
     * Find stores by state
     * @param state State code
     * @return List of stores in the state
     */
    List<Store> findByState(String state);
    
    /**
     * Find stores by city and state
     * @param city City name
     * @param state State code
     * @return List of stores matching criteria
     */
    List<Store> findByCityAndState(String city, String state);
    
    /**
     * Find all active stores
     * @param isActive Active status
     * @return List of active stores
     */
    List<Store> findByIsActive(Boolean isActive);
    
    /**
     * Find stores by manager name
     * @param manager Manager name
     * @return List of stores managed by the person
     */
    List<Store> findByManagerContainingIgnoreCase(String manager);
    
    /**
     * Check if a store with the given number exists
     * @param number Store number
     * @return True if exists, false otherwise
     */
    boolean existsByNumber(String number);
    
    /**
     * Count stores by state
     * @param state State code
     * @return Count of stores in the state
     */
    long countByState(String state);
    
    /**
     * Count active stores
     * @param isActive Active status
     * @return Count of active stores
     */
    long countByIsActive(Boolean isActive);
}
