package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * Session this sale belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private Session session;

    /**
     * Store where sale occurred
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @ToString.Exclude
    private Store store;

    /**
     * Cashier who processed the sale
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    @ToString.Exclude
    private Cashier cashier;

    /**
     * Line items in this sale
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SaleLineItem> lineItems = new ArrayList<>();

    /**
     * Payments applied to this sale
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Payment> payments = new ArrayList<>();
    
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
        this.lineItems = new ArrayList<>();
        this.payments = new ArrayList<>();
    }
    
    /**
     * Constructor with session, store, and cashier
     * @param session Session entity
     * @param store Store entity
     * @param cashier Cashier entity
     */
    public Sale(Session session, Store store, Cashier cashier) {
        this();
        this.session = session;
        this.store = store;
        this.cashier = cashier;
    }
}
