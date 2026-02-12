package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an Item in the Point of Sale System.
 * Items are products that can be sold in stores.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Item number - unique identifier
     */
    @Column(nullable = false, unique = true, length = 50)
    private String number;
    
    /**
     * Item description/name
     */
    @Column(nullable = false, length = 500)
    private String description;
    
    /**
     * Current price of the item
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Cost of the item (for profit calculation)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
    
    /**
     * Quantity in stock
     */
    @Column(nullable = false)
    private Integer quantity = 0;
    
    /**
     * Minimum stock level for reordering
     */
    @Column
    private Integer minQuantity;
    
    /**
     * Maximum stock level
     */
    @Column
    private Integer maxQuantity;
    
    /**
     * Tax category ID reference (simplified - will add proper relationship later)
     */
    @Column(name = "tax_category_id")
    private Long taxCategoryId;
    
    /**
     * Store ID reference (simplified - will add proper relationship later)
     */
    @Column(name = "store_id")
    private Long storeId;
    
    /**
     * Item barcode/UPC
     */
    @Column(length = 50)
    private String barcode;
    
    /**
     * Item SKU (Stock Keeping Unit)
     */
    @Column(length = 50)
    private String sku;
    
    /**
     * Item brand
     */
    @Column(length = 100)
    private String brand;
    
    /**
     * Item category/department
     */
    @Column(length = 100)
    private String category;
    
    /**
     * Active status of the item
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Taxable status
     */
    @Column(nullable = false)
    private Boolean isTaxable = true;
    
    /**
     * Date when item was added
     */
    @Column
    private LocalDateTime createdDate;
    
    /**
     * Date when item was last modified
     */
    @Column
    private LocalDateTime modifiedDate;
    
    /**
     * Constructor with item number and description
     * @param number Item number
     * @param description Item description
     */
    public Item(String number, String description) {
        this.number = number;
        this.description = description;
        this.quantity = 0;
        this.isActive = true;
        this.isTaxable = true;
        this.createdDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with item number, description, and price
     * @param number Item number
     * @param description Item description
     * @param price Item price
     */
    public Item(String number, String description, BigDecimal price) {
        this.number = number;
        this.description = description;
        this.price = price;
        this.quantity = 0;
        this.isActive = true;
        this.isTaxable = true;
        this.createdDate = LocalDateTime.now();
    }
    
    /**
     * Check if item needs reordering
     * @return true if quantity is below minimum
     */
    public boolean needsReorder() {
        return minQuantity != null && quantity <= minQuantity;
    }
    
    /**
     * Check if item is in stock
     * @return true if quantity is greater than 0
     */
    public boolean isInStock() {
        return quantity > 0;
    }
    
    /**
     * Update modified date
     */
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}
