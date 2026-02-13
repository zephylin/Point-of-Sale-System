package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a tax rate for a specific tax category.
 * Each tax rate has an effective date and percentage.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "tax_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Tax rate percentage (e.g., 0.08 for 8%)
     */
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal rate;
    
    /**
     * Date when this tax rate becomes effective
     */
    @Column(nullable = false)
    private LocalDate effectiveDate;
    
    /**
     * Tax category ID reference
     */
    @Column(name = "tax_category_id", nullable = false)
    private Long taxCategoryId;
    
    /**
     * Description of the tax rate
     */
    @Column(length = 500)
    private String description;
    
    /**
     * Active status
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Constructor with rate and effective date
     * @param rate Tax rate percentage
     * @param effectiveDate Effective date
     */
    public TaxRate(BigDecimal rate, LocalDate effectiveDate) {
        this.rate = rate;
        this.effectiveDate = effectiveDate;
        this.isActive = true;
    }
    
    /**
     * Constructor with all required fields
     * @param rate Tax rate percentage
     * @param effectiveDate Effective date
     * @param taxCategoryId Tax category ID
     */
    public TaxRate(BigDecimal rate, LocalDate effectiveDate, Long taxCategoryId) {
        this.rate = rate;
        this.effectiveDate = effectiveDate;
        this.taxCategoryId = taxCategoryId;
        this.isActive = true;
    }
    
    /**
     * Check if this tax rate is effective for a given date
     * @param date Date to check
     * @return true if effective
     */
    public boolean isEffectiveOn(LocalDate date) {
        return !date.isBefore(effectiveDate);
    }
}
