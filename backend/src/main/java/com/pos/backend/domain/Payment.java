package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a payment for a sale.
 * Supports CASH, CREDIT, and CHECK payment types via a type discriminator field.
 *
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sale this payment belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    @ToString.Exclude
    private Sale sale;

    /**
     * Payment type: CASH, CREDIT, or CHECK
     */
    @Column(name = "payment_type", nullable = false, length = 20)
    private String paymentType;

    /**
     * Payment amount applied to the sale
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Amount actually tendered by the customer
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal amountTendered;

    /**
     * Change returned to the customer (cash payments)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal changeDue;

    /**
     * Whether this payment counts as cash for cash drawer purposes
     */
    @Column(nullable = false)
    private Boolean countAsCash = false;

    /**
     * Date and time the payment was processed
     */
    @Column(nullable = false)
    private LocalDateTime paymentDateTime;

    // ==================== Authorized payment fields (Credit/Check) ====================

    /**
     * Authorization code returned by payment processor
     */
    @Column(length = 100)
    private String authorizationCode;

    /**
     * Whether the payment was authorized
     */
    @Column
    private Boolean authorized;

    // ==================== Credit-specific fields ====================

    /**
     * Credit card type (Visa, MasterCard, Amex, etc.)
     */
    @Column(length = 50)
    private String cardType;

    /**
     * Last four digits of card number (never store full card numbers)
     */
    @Column(length = 4)
    private String cardLastFour;

    /**
     * Card expiration date
     */
    @Column
    private LocalDate expirationDate;

    // ==================== Check-specific fields ====================

    /**
     * Check routing number
     */
    @Column(length = 20)
    private String routingNumber;

    /**
     * Check account number
     */
    @Column(length = 20)
    private String accountNumber;

    /**
     * Check number
     */
    @Column(length = 50)
    private String checkNumber;

    /**
     * Constructor for a cash payment
     */
    public Payment(Sale sale, BigDecimal amount, BigDecimal amountTendered) {
        this.sale = sale;
        this.paymentType = "CASH";
        this.amount = amount;
        this.amountTendered = amountTendered;
        this.countAsCash = true;
        this.paymentDateTime = LocalDateTime.now();
        if (amountTendered != null && amount != null) {
            this.changeDue = amountTendered.subtract(amount).max(BigDecimal.ZERO);
        }
    }
}
