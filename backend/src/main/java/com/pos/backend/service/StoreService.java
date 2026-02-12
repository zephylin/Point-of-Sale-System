package com.pos.backend.service;

import com.pos.backend.domain.Store;
import com.pos.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Store entity.
 * Contains business logic for store operations.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreService {
    
    private final StoreRepository storeRepository;
    
    /**
     * Get all stores
     * @return List of all stores
     */
    @Transactional(readOnly = true)
    public List<Store> findAll() {
        log.debug("Finding all stores");
        return storeRepository.findAll();
    }
    
    /**
     * Get store by ID
     * @param id Store ID
     * @return Optional containing the store if found
     */
    @Transactional(readOnly = true)
    public Optional<Store> findById(Long id) {
        log.debug("Finding store by id: {}", id);
        return storeRepository.findById(id);
    }
    
    /**
     * Get store by store number
     * @param number Store number
     * @return Optional containing the store if found
     */
    @Transactional(readOnly = true)
    public Optional<Store> findByNumber(String number) {
        log.debug("Finding store by number: {}", number);
        return storeRepository.findByNumber(number);
    }
    
    /**
     * Search stores by name
     * @param name Store name keyword
     * @return List of matching stores
     */
    @Transactional(readOnly = true)
    public List<Store> searchByName(String name) {
        log.debug("Searching stores by name: {}", name);
        return storeRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Find stores by city
     * @param city City name
     * @return List of stores in the city
     */
    @Transactional(readOnly = true)
    public List<Store> findByCity(String city) {
        log.debug("Finding stores by city: {}", city);
        return storeRepository.findByCity(city);
    }
    
    /**
     * Find stores by state
     * @param state State code
     * @return List of stores in the state
     */
    @Transactional(readOnly = true)
    public List<Store> findByState(String state) {
        log.debug("Finding stores by state: {}", state);
        return storeRepository.findByState(state.toUpperCase());
    }
    
    /**
     * Find stores by city and state
     * @param city City name
     * @param state State code
     * @return List of stores matching criteria
     */
    @Transactional(readOnly = true)
    public List<Store> findByCityAndState(String city, String state) {
        log.debug("Finding stores by city: {} and state: {}", city, state);
        return storeRepository.findByCityAndState(city, state.toUpperCase());
    }
    
    /**
     * Get all active stores
     * @return List of active stores
     */
    @Transactional(readOnly = true)
    public List<Store> findAllActive() {
        log.debug("Finding all active stores");
        return storeRepository.findByIsActive(true);
    }
    
    /**
     * Find stores by manager
     * @param manager Manager name
     * @return List of stores managed by the person
     */
    @Transactional(readOnly = true)
    public List<Store> findByManager(String manager) {
        log.debug("Finding stores by manager: {}", manager);
        return storeRepository.findByManagerContainingIgnoreCase(manager);
    }
    
    /**
     * Create a new store
     * @param store Store to create
     * @return Created store
     * @throws IllegalArgumentException if validation fails
     */
    public Store create(Store store) {
        log.info("Creating new store: {}", store.getNumber());
        
        // Validate
        validateStore(store);
        
        // Check for duplicates
        if (storeRepository.existsByNumber(store.getNumber())) {
            throw new IllegalArgumentException("Store with number '" + store.getNumber() + "' already exists");
        }
        
        // Normalize state to uppercase
        if (store.getState() != null) {
            store.setState(store.getState().toUpperCase());
        }
        
        // Set defaults
        if (store.getIsActive() == null) {
            store.setIsActive(true);
        }
        if (store.getOpenedDate() == null) {
            store.setOpenedDate(LocalDateTime.now());
        }
        
        Store saved = storeRepository.save(store);
        log.info("Created store with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Update an existing store
     * @param id Store ID
     * @param store Updated store data
     * @return Updated store
     * @throws IllegalArgumentException if store not found or validation fails
     */
    public Store update(Long id, Store store) {
        log.info("Updating store with id: {}", id);
        
        Store existing = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));
        
        // Validate
        validateStore(store);
        
        // Check for duplicate number (excluding current record)
        Optional<Store> duplicate = storeRepository.findByNumber(store.getNumber());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Store with number '" + store.getNumber() + "' already exists");
        }
        
        // Normalize state
        if (store.getState() != null) {
            store.setState(store.getState().toUpperCase());
        }
        
        // Update fields
        existing.setNumber(store.getNumber());
        existing.setName(store.getName());
        existing.setAddress(store.getAddress());
        existing.setCity(store.getCity());
        existing.setState(store.getState());
        existing.setZip(store.getZip());
        existing.setPhone(store.getPhone());
        existing.setEmail(store.getEmail());
        existing.setManager(store.getManager());
        existing.setIsActive(store.getIsActive());
        
        Store updated = storeRepository.save(existing);
        log.info("Updated store with id: {}", id);
        return updated;
    }
    
    /**
     * Delete a store
     * @param id Store ID
     * @throws IllegalArgumentException if store not found
     */
    public void delete(Long id) {
        log.info("Deleting store with id: {}", id);
        
        if (!storeRepository.existsById(id)) {
            throw new IllegalArgumentException("Store not found with id: " + id);
        }
        
        storeRepository.deleteById(id);
        log.info("Deleted store with id: {}", id);
    }
    
    /**
     * Deactivate a store (soft delete)
     * @param id Store ID
     * @return Deactivated store
     * @throws IllegalArgumentException if store not found
     */
    public Store deactivate(Long id) {
        log.info("Deactivating store with id: {}", id);
        
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));
        
        store.setIsActive(false);
        store.setClosedDate(LocalDateTime.now());
        
        Store updated = storeRepository.save(store);
        log.info("Deactivated store with id: {}", id);
        return updated;
    }
    
    /**
     * Reactivate a store
     * @param id Store ID
     * @return Reactivated store
     * @throws IllegalArgumentException if store not found
     */
    public Store activate(Long id) {
        log.info("Activating store with id: {}", id);
        
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));
        
        store.setIsActive(true);
        store.setClosedDate(null);
        
        Store updated = storeRepository.save(store);
        log.info("Activated store with id: {}", id);
        return updated;
    }
    
    /**
     * Count all stores
     * @return Total count
     */
    @Transactional(readOnly = true)
    public long count() {
        return storeRepository.count();
    }
    
    /**
     * Count active stores
     * @return Count of active stores
     */
    @Transactional(readOnly = true)
    public long countActive() {
        return storeRepository.countByIsActive(true);
    }
    
    /**
     * Count stores by state
     * @param state State code
     * @return Count of stores in the state
     */
    @Transactional(readOnly = true)
    public long countByState(String state) {
        return storeRepository.countByState(state.toUpperCase());
    }
    
    /**
     * Validate store data
     * @param store Store to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        
        if (store.getNumber() == null || store.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Store number is required");
        }
        
        if (store.getName() == null || store.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Store name is required");
        }
        
        if (store.getNumber().length() > 50) {
            throw new IllegalArgumentException("Store number must not exceed 50 characters");
        }
        
        if (store.getName().length() > 200) {
            throw new IllegalArgumentException("Store name must not exceed 200 characters");
        }
        
        // Validate state code (if provided)
        if (store.getState() != null && !store.getState().isEmpty()) {
            if (store.getState().length() != 2) {
                throw new IllegalArgumentException("State must be a 2-letter code");
            }
        }
        
        // Validate ZIP code format (if provided)
        if (store.getZip() != null && !store.getZip().isEmpty()) {
            if (!store.getZip().matches("\\d{5}(-\\d{4})?")) {
                throw new IllegalArgumentException("ZIP code must be in format 12345 or 12345-6789");
            }
        }
        
        // Validate email format (if provided)
        if (store.getEmail() != null && !store.getEmail().isEmpty()) {
            if (!store.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
    }
}
