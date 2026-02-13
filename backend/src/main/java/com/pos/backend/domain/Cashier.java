package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * Person ID reference
     */
    @Column(name = "person_id")
    private Long personId;
    
    /**
     * Store ID where cashier works
     */
    @Column(name = "store_id")
    private Long storeId;
    
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
     * Constructor with number, password, and person ID
     * @param number Cashier number
     * @param password Cashier password
     * @param personId Person ID
     */
    public Cashier(String number, String password, Long personId) {
        this.number = number;
        this.password = password;
        this.personId = personId;
        this.isActive = true;
        this.hireDate = LocalDateTime.now();
        this.role = "Cashier";
    }
}
