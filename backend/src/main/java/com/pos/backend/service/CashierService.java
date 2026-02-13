package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.repository.CashierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return cashierRepository.findByStoreId(storeId);
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
    
    public Cashier update(Long id, Cashier cashier) {
        Cashier existing = cashierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cashier not found with id: " + id));
        validateCashier(cashier);
        
        Optional<Cashier> duplicate = cashierRepository.findByNumber(cashier.getNumber());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Cashier with number '" + cashier.getNumber() + "' already exists");
        }
        
        existing.setNumber(cashier.getNumber());
        existing.setPassword(cashier.getPassword());
        existing.setPersonId(cashier.getPersonId());
        existing.setStoreId(cashier.getStoreId());
        existing.setIsActive(cashier.getIsActive());
        existing.setRole(cashier.getRole());
        return cashierRepository.save(existing);
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
        return cashierRepository.countByStoreId(storeId);
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
