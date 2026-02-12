package com.pos.backend.repository;

import com.pos.backend.domain.TaxCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaxCategory entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Repository
public interface TaxCategoryRepository extends JpaRepository<TaxCategory, Long> {
    
    /**
     * Find tax category by category name
     * @param category The category name
     * @return Optional containing the tax category if found
     */
    Optional<TaxCategory> findByCategory(String category);
    
    /**
     * Find tax category by category name (case-insensitive)
     * @param category The category name
     * @return Optional containing the tax category if found
     */
    Optional<TaxCategory> findByCategoryIgnoreCase(String category);
    
    /**
     * Find all active tax categories
     * @param isActive Active status
     * @return List of active tax categories
     */
    List<TaxCategory> findByIsActive(Boolean isActive);
    
    /**
     * Find tax categories by category name containing keyword
     * @param keyword The search keyword
     * @return List of matching tax categories
     */
    List<TaxCategory> findByCategoryContainingIgnoreCase(String keyword);
    
    /**
     * Check if a tax category with the given name already exists
     * @param category The category name
     * @return True if exists, false otherwise
     */
    boolean existsByCategory(String category);
    
    /**
     * Check if a tax category with the given name exists (case-insensitive)
     * @param category The category name
     * @return True if exists, false otherwise
     */
    boolean existsByCategoryIgnoreCase(String category);
}
