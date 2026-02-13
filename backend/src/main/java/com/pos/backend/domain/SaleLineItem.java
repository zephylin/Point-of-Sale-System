package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing a line item in a sale.
 * Each line item represents a quantity of a specific item.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "sale_line_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleLineItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Sale ID that this line item belongs to
     */
    @Column(name = "sale_id", nullable = false)
    private Long saleId;
    
    /**
     * Item ID being sold
     */
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    /**
     * Quantity of the item
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Unit price at time of sale
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    /**
     * Extended price (quantity * unitPrice)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal extendedPrice;
    
    /**
     * Tax rate applied
     */
    @Column(precision = 5, scale = 4)
    private BigDecimal taxRate;
    
    /**
     * Tax amount for this line item
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    /**
     * Total price (extendedPrice + taxAmount)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    /**
     * Discount applied (if any)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal discount;
    
    /**
     * Notes about this line item
     */
    @Column(length = 500)
    private String notes;
    
    /**
     * Constructor with sale ID, item ID, and quantity
     * @param saleId Sale ID
     * @param itemId Item ID
     * @param quantity Quantity
     */
    public SaleLineItem(Long saleId, Long itemId, Integer quantity) {
        this.saleId = saleId;
        this.itemId = itemId;
        this.quantity = quantity;
    }
    
    /**
     * Constructor with all required fields
     * @param saleId Sale ID
     * @param itemId Item ID
     * @param quantity Quantity
     * @param unitPrice Unit price
     */
    public SaleLineItem(Long saleId, Long itemId, Integer quantity, BigDecimal unitPrice) {
        this.saleId = saleId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.extendedPrice = unitPrice.multiply(new BigDecimal(quantity));
        this.taxAmount = BigDecimal.ZERO;
        this.totalPrice = this.extendedPrice;
    }
}
