package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a sale transaction.
 * A sale contains multiple sale line items and payments.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "sales")
@Data
@AllArgsConstructor
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Session ID for this sale
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;
    
    /**
     * Store ID where sale occurred
     */
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    /**
     * Cashier ID who processed the sale
     */
    @Column(name = "cashier_id", nullable = false)
    private Long cashierId;
    
    /**
     * Sale date and time
     */
    @Column(nullable = false)
    private LocalDateTime dateTime;
    
    /**
     * Subtotal (before tax)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    /**
     * Tax amount
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;
    
    /**
     * Total (subtotal + tax)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    /**
     * Amount paid
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;
    
    /**
     * Change given
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal change = BigDecimal.ZERO;
    
    /**
     * Tax free flag (tax exempt transaction)
     */
    @Column(nullable = false)
    private Boolean taxFree = false;
    
    /**
     * Sale status (PENDING, COMPLETED, VOID, RETURNED)
     */
    @Column(length = 20, nullable = false)
    private String status = "PENDING";
    
    /**
     * Payment method (CASH, CREDIT, CHECK, MIXED)
     */
    @Column(length = 20)
    private String paymentMethod;
    
    /**
     * Notes about the sale
     */
    @Column(length = 1000)
    private String notes;
    
    /**
     * Default constructor
     */
    public Sale() {
        this.dateTime = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.change = BigDecimal.ZERO;
        this.taxFree = false;
        this.status = "PENDING";
    }
    
    /**
     * Constructor with session, store, and cashier
     * @param sessionId Session ID
     * @param storeId Store ID
     * @param cashierId Cashier ID
     */
    public Sale(Long sessionId, Long storeId, Long cashierId) {
        this();
        this.sessionId = sessionId;
        this.storeId = storeId;
        this.cashierId = cashierId;
    }
}
