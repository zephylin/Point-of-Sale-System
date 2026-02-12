package com.pos.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a Store in the Point of Sale System.
 * Each store has its own inventory, cashiers, and registers.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Store number - unique identifier
     */
    @Column(nullable = false, unique = true, length = 50)
    private String number;
    
    /**
     * Store name
     */
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Store address
     */
    @Column(length = 500)
    private String address;
    
    /**
     * Store city
     */
    @Column(length = 100)
    private String city;
    
    /**
     * Store state (2-letter code)
     */
    @Column(length = 2)
    private String state;
    
    /**
     * Store ZIP code
     */
    @Column(length = 10)
    private String zip;
    
    /**
     * Store phone number
     */
    @Column(length = 20)
    private String phone;
    
    /**
     * Store email
     */
    @Column(length = 100)
    private String email;
    
    /**
     * Store manager name
     */
    @Column(length = 100)
    private String manager;
    
    /**
     * Active status of the store
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date when store was opened
     */
    @Column
    private LocalDateTime openedDate;
    
    /**
     * Date when store was closed (if applicable)
     */
    @Column
    private LocalDateTime closedDate;
    
    /**
     * Constructor with store number and name
     * @param number Store number
     * @param name Store name
     */
    public Store(String number, String name) {
        this.number = number;
        this.name = name;
        this.isActive = true;
        this.openedDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with store number, name, and location
     * @param number Store number
     * @param name Store name
     * @param address Store address
     * @param city Store city
     * @param state Store state
     * @param zip Store ZIP code
     */
    public Store(String number, String name, String address, String city, String state, String zip) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.isActive = true;
        this.openedDate = LocalDateTime.now();
    }
}
