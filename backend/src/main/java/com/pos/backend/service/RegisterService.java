package com.pos.backend.service;

import com.pos.backend.domain.Register;
import com.pos.backend.repository.RegisterRepository;
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
public class RegisterService {
    
    private final RegisterRepository registerRepository;
    
    @Transactional(readOnly = true)
    public List<Register> findAll() {
        return registerRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Register> findById(Long id) {
        return registerRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Register> findByNumber(String number) {
        return registerRepository.findByNumber(number);
    }
    
    @Transactional(readOnly = true)
    public List<Register> findByStore(Long storeId) {
        return registerRepository.findByStoreId(storeId);
    }
    
    @Transactional(readOnly = true)
    public List<Register> findAllActive() {
        return registerRepository.findByIsActive(true);
    }
    
    @Transactional(readOnly = true)
    public List<Register> findByStatus(String status) {
        return registerRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Register> findByStoreAndStatus(Long storeId, String status) {
        return registerRepository.findByStoreIdAndStatus(storeId, status);
    }
    
    public Register create(Register register) {
        validateRegister(register);
        if (registerRepository.existsByNumber(register.getNumber())) {
            throw new IllegalArgumentException("Register with number '" + register.getNumber() + "' already exists");
        }
        if (register.getIsActive() == null) {
            register.setIsActive(true);
        }
        if (register.getStatus() == null) {
            register.setStatus("CLOSED");
        }
        if (register.getInstalledDate() == null) {
            register.setInstalledDate(LocalDateTime.now());
        }
        return registerRepository.save(register);
    }
    
    public Register update(Long id, Register register) {
        Register existing = registerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Register not found with id: " + id));
        validateRegister(register);
        
        Optional<Register> duplicate = registerRepository.findByNumber(register.getNumber());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Register with number '" + register.getNumber() + "' already exists");
        }
        
        existing.setNumber(register.getNumber());
        existing.setStoreId(register.getStoreId());
        existing.setDescription(register.getDescription());
        existing.setIsActive(register.getIsActive());
        existing.setStatus(register.getStatus());
        return registerRepository.save(existing);
    }
    
    public Register updateStatus(Long id, String status) {
        Register register = registerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Register not found with id: " + id));
        register.setStatus(status);
        return registerRepository.save(register);
    }
    
    public void delete(Long id) {
        if (!registerRepository.existsById(id)) {
            throw new IllegalArgumentException("Register not found with id: " + id);
        }
        registerRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return registerRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countByStore(Long storeId) {
        return registerRepository.countByStoreId(storeId);
    }
    
    private void validateRegister(Register register) {
        if (register == null) {
            throw new IllegalArgumentException("Register cannot be null");
        }
        if (register.getNumber() == null || register.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required");
        }
        if (register.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required");
        }
    }
}
