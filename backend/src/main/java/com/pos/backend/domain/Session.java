package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a cashier work session.
 * A session tracks when a cashier is logged into a register.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Cashier ID for this session
     */
    @Column(name = "cashier_id", nullable = false)
    private Long cashierId;
    
    /**
     * Register ID for this session
     */
    @Column(name = "register_id", nullable = false)
    private Long registerId;
    
    /**
     * Session start date and time
     */
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    
    /**
     * Session end date and time
     */
    @Column
    private LocalDateTime endDateTime;
    
    /**
     * Starting cash amount in drawer
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal startingCash;
    
    /**
     * Ending cash amount in drawer
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal endingCash;
    
    /**
     * Expected cash amount
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal expectedCash;
    
    /**
     * Cash variance (over/short)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal cashVariance;
    
    /**
     * Session status (ACTIVE, CLOSED, SUSPENDED)
     */
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";
    
    /**
     * Notes about the session
     */
    @Column(length = 1000)
    private String notes;
    
    /**
     * Constructor with cashier and register
     * @param cashierId Cashier ID
     * @param registerId Register ID
     */
    public Session(Long cashierId, Long registerId) {
        this.cashierId = cashierId;
        this.registerId = registerId;
        this.startDateTime = LocalDateTime.now();
        this.status = "ACTIVE";
    }
    
    /**
     * Constructor with cashier, register, and starting cash
     * @param cashierId Cashier ID
     * @param registerId Register ID
     * @param startingCash Starting cash amount
     */
    public Session(Long cashierId, Long registerId, BigDecimal startingCash) {
        this.cashierId = cashierId;
        this.registerId = registerId;
        this.startDateTime = LocalDateTime.now();
        this.startingCash = startingCash;
        this.status = "ACTIVE";
    }
}
