package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entity representing a POS register/terminal.
 * Each register is assigned to a store.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "registers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Register {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Register number (unique identifier)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String number;
    
    /**
     * Store where register is located
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @ToString.Exclude
    private Store store;
    
    /**
     * Register description/location
     */
    @Column(length = 200)
    private String description;
    
    /**
     * Active status
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Current status (OPEN, CLOSED, MAINTENANCE)
     */
    @Column(length = 20)
    private String status = "CLOSED";
    
    /**
     * Date when register was installed
     */
    @Column
    private LocalDateTime installedDate;
    
    /**
     * Last maintenance date
     */
    @Column
    private LocalDateTime lastMaintenanceDate;
    
    /**
     * Constructor with register number
     * @param number Register number
     */
    public Register(String number) {
        this.number = number;
        this.isActive = true;
        this.status = "CLOSED";
        this.installedDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with register number and store
     * @param number Register number
     * @param store Store entity
     */
    public Register(String number, Store store) {
        this.number = number;
        this.store = store;
        this.isActive = true;
        this.status = "CLOSED";
        this.installedDate = LocalDateTime.now();
    }
}
