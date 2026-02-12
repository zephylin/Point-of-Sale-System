package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a tax category for items.
 * Each tax category groups items with similar tax rates.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "tax_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Category name (e.g., "Food", "General Merchandise", "Alcohol")
     */
    @Column(nullable = false, unique = true, length = 100)
    private String category;
    
    /**
     * Description of the tax category
     */
    @Column(length = 500)
    private String description;
    
    /**
     * Active status of the tax category
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Constructor with category name
     * @param category The category name
     */
    public TaxCategory(String category) {
        this.category = category;
        this.isActive = true;
    }
    
    /**
     * Constructor with category and description
     * @param category The category name
     * @param description Description of the category
     */
    public TaxCategory(String category, String description) {
        this.category = category;
        this.description = description;
        this.isActive = true;
    }
}
