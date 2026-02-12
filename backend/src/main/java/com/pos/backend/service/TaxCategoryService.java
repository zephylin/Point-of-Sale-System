package com.pos.backend.service;

import com.pos.backend.domain.TaxCategory;
import com.pos.backend.repository.TaxCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for TaxCategory entity.
 * Contains business logic for tax category operations.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaxCategoryService {
    
    private final TaxCategoryRepository taxCategoryRepository;
    
    /**
     * Get all tax categories
     * @return List of all tax categories
     */
    @Transactional(readOnly = true)
    public List<TaxCategory> findAll() {
        log.debug("Finding all tax categories");
        return taxCategoryRepository.findAll();
    }
    
    /**
     * Get tax category by ID
     * @param id Tax category ID
     * @return Optional containing the tax category if found
     */
    @Transactional(readOnly = true)
    public Optional<TaxCategory> findById(Long id) {
        log.debug("Finding tax category by id: {}", id);
        return taxCategoryRepository.findById(id);
    }
    
    /**
     * Get tax category by category name
     * @param category Category name
     * @return Optional containing the tax category if found
     */
    @Transactional(readOnly = true)
    public Optional<TaxCategory> findByCategory(String category) {
        log.debug("Finding tax category by category: {}", category);
        return taxCategoryRepository.findByCategoryIgnoreCase(category);
    }
    
    /**
     * Search tax categories by keyword
     * @param keyword Search keyword
     * @return List of matching tax categories
     */
    @Transactional(readOnly = true)
    public List<TaxCategory> search(String keyword) {
        log.debug("Searching tax categories with keyword: {}", keyword);
        return taxCategoryRepository.findByCategoryContainingIgnoreCase(keyword);
    }
    
    /**
     * Get all active tax categories
     * @return List of active tax categories
     */
    @Transactional(readOnly = true)
    public List<TaxCategory> findAllActive() {
        log.debug("Finding all active tax categories");
        return taxCategoryRepository.findByIsActive(true);
    }
    
    /**
     * Create a new tax category
     * @param taxCategory Tax category to create
     * @return Created tax category
     * @throws IllegalArgumentException if validation fails
     */
    public TaxCategory create(TaxCategory taxCategory) {
        log.info("Creating new tax category: {}", taxCategory.getCategory());
        
        // Validate
        validateTaxCategory(taxCategory);
        
        // Check for duplicates
        if (taxCategoryRepository.existsByCategoryIgnoreCase(taxCategory.getCategory())) {
            throw new IllegalArgumentException("Tax category with name '" + taxCategory.getCategory() + "' already exists");
        }
        
        // Set defaults
        if (taxCategory.getIsActive() == null) {
            taxCategory.setIsActive(true);
        }
        
        TaxCategory saved = taxCategoryRepository.save(taxCategory);
        log.info("Created tax category with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Update an existing tax category
     * @param id Tax category ID
     * @param taxCategory Updated tax category data
     * @return Updated tax category
     * @throws IllegalArgumentException if tax category not found or validation fails
     */
    public TaxCategory update(Long id, TaxCategory taxCategory) {
        log.info("Updating tax category with id: {}", id);
        
        TaxCategory existing = taxCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax category not found with id: " + id));
        
        // Validate
        validateTaxCategory(taxCategory);
        
        // Check for duplicate name (excluding current record)
        Optional<TaxCategory> duplicate = taxCategoryRepository.findByCategoryIgnoreCase(taxCategory.getCategory());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tax category with name '" + taxCategory.getCategory() + "' already exists");
        }
        
        // Update fields
        existing.setCategory(taxCategory.getCategory());
        existing.setDescription(taxCategory.getDescription());
        existing.setIsActive(taxCategory.getIsActive());
        
        TaxCategory updated = taxCategoryRepository.save(existing);
        log.info("Updated tax category with id: {}", id);
        return updated;
    }
    
    /**
     * Delete a tax category
     * @param id Tax category ID
     * @throws IllegalArgumentException if tax category not found
     */
    public void delete(Long id) {
        log.info("Deleting tax category with id: {}", id);
        
        if (!taxCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax category not found with id: " + id);
        }
        
        taxCategoryRepository.deleteById(id);
        log.info("Deleted tax category with id: {}", id);
    }
    
    /**
     * Deactivate a tax category (soft delete)
     * @param id Tax category ID
     * @return Deactivated tax category
     * @throws IllegalArgumentException if tax category not found
     */
    public TaxCategory deactivate(Long id) {
        log.info("Deactivating tax category with id: {}", id);
        
        TaxCategory taxCategory = taxCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax category not found with id: " + id));
        
        taxCategory.setIsActive(false);
        TaxCategory updated = taxCategoryRepository.save(taxCategory);
        log.info("Deactivated tax category with id: {}", id);
        return updated;
    }
    
    /**
     * Count all tax categories
     * @return Total count
     */
    @Transactional(readOnly = true)
    public long count() {
        return taxCategoryRepository.count();
    }
    
    /**
     * Validate tax category data
     * @param taxCategory Tax category to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTaxCategory(TaxCategory taxCategory) {
        if (taxCategory == null) {
            throw new IllegalArgumentException("Tax category cannot be null");
        }
        
        if (taxCategory.getCategory() == null || taxCategory.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        
        if (taxCategory.getCategory().length() > 100) {
            throw new IllegalArgumentException("Category name must not exceed 100 characters");
        }
        
        if (taxCategory.getDescription() != null && taxCategory.getDescription().length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters");
        }
    }
}
