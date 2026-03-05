package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entity representing a cashier in the POS system.
 * Each cashier is associated with a person.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "cashiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cashier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Cashier employee number (unique identifier)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String number;
    
    /**
     * Cashier password (should be hashed in production)
     */
    @Column(nullable = false, length = 255)
    private String password;
    
    /**
     * Person associated with this cashier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    private Person person;

    /**
     * Store where cashier works
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @ToString.Exclude
    private Store store;
    
    /**
     * Active status
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date when cashier was hired
     */
    @Column
    private LocalDateTime hireDate;
    
    /**
     * Date when cashier was terminated
     */
    @Column
    private LocalDateTime terminationDate;
    
    /**
     * Cashier role (e.g., "Cashier", "Supervisor", "Manager")
     */
    @Column(length = 50)
    private String role;
    
    /**
     * Constructor with number and password
     * @param number Cashier number
     * @param password Cashier password
     */
    public Cashier(String number, String password) {
        this.number = number;
        this.password = password;
        this.isActive = true;
        this.hireDate = LocalDateTime.now();
        this.role = "Cashier";
    }
    
    /**
     * Constructor with number, password, and person
     * @param number Cashier number
     * @param password Cashier password
     * @param person Person entity
     */
    public Cashier(String number, String password, Person person) {
        this.number = number;
        this.password = password;
        this.person = person;
        this.isActive = true;
        this.hireDate = LocalDateTime.now();
        this.role = "Cashier";
    }
}
