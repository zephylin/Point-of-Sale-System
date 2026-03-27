package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Person;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.PersonRepository;
import com.pos.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CashierService {
    
    private final CashierRepository cashierRepository;
    private final PersonRepository personRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public List<Cashier> findAll() {
        return cashierRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Cashier> findById(Long id) {
        return cashierRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Cashier> findByNumber(String number) {
        return cashierRepository.findByNumber(number);
    }
    
    @Transactional(readOnly = true)
    public List<Cashier> findByStore(Long storeId) {
        return cashierRepository.findByStore_Id(storeId);
    }
    
    @Transactional(readOnly = true)
    public List<Cashier> findAllActive() {
        return cashierRepository.findByIsActive(true);
    }
    
    @Transactional(readOnly = true)
    public List<Cashier> findByRole(String role) {
        return cashierRepository.findByRole(role);
    }
    
    public Cashier create(Cashier cashier) {
        validateCashier(cashier);
        if (cashierRepository.existsByNumber(cashier.getNumber())) {
            throw new IllegalArgumentException("Cashier with number '" + cashier.getNumber() + "' already exists");
        }
        cashier.setPassword(passwordEncoder.encode(cashier.getPassword()));
        if (cashier.getIsActive() == null) {
            cashier.setIsActive(true);
        }
        if (cashier.getHireDate() == null) {
            cashier.setHireDate(LocalDateTime.now());
        }
        if (cashier.getRole() == null) {
            cashier.setRole("Cashier");
        }
        return cashierRepository.save(cashier);
    }

    /**
     * Create a cashier, resolving person and store from IDs
     */
    public Cashier createWithIds(Cashier cashier, Long personId, Long storeId) {
        resolveRelationships(cashier, personId, storeId);
        return create(cashier);
    }
    
    public Cashier update(Long id, Cashier cashier) {
        Cashier existing = cashierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cashier not found with id: " + id));
        validateCashier(cashier);
        
        Optional<Cashier> duplicate = cashierRepository.findByNumber(cashier.getNumber());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Cashier with number '" + cashier.getNumber() + "' already exists");
        }
        
        existing.setNumber(cashier.getNumber());
        existing.setPassword(passwordEncoder.encode(cashier.getPassword()));
        existing.setPerson(cashier.getPerson());
        existing.setStore(cashier.getStore());
        existing.setIsActive(cashier.getIsActive());
        existing.setRole(cashier.getRole());
        return cashierRepository.save(existing);
    }

    /**
     * Update a cashier, resolving person and store from IDs
     */
    public Cashier updateWithIds(Long id, Cashier cashier, Long personId, Long storeId) {
        resolveRelationships(cashier, personId, storeId);
        return update(id, cashier);
    }
    
    public Cashier terminate(Long id) {
        Cashier cashier = cashierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cashier not found with id: " + id));
        cashier.setIsActive(false);
        cashier.setTerminationDate(LocalDateTime.now());
        return cashierRepository.save(cashier);
    }
    
    public void delete(Long id) {
        if (!cashierRepository.existsById(id)) {
            throw new IllegalArgumentException("Cashier not found with id: " + id);
        }
        cashierRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return cashierRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countByStore(Long storeId) {
        return cashierRepository.countByStore_Id(storeId);
    }

    private void resolveRelationships(Cashier cashier, Long personId, Long storeId) {
        if (personId != null) {
            Person person = personRepository.findById(personId)
                    .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
            cashier.setPerson(person);
        }
        if (storeId != null) {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));
            cashier.setStore(store);
        }
    }
    
    private void validateCashier(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }
        if (cashier.getNumber() == null || cashier.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Cashier number is required");
        }
        if (cashier.getPassword() == null || cashier.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
