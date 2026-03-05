package com.pos.backend.service;

import com.pos.backend.domain.Register;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.RegisterRepository;
import com.pos.backend.repository.StoreRepository;
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
    private final StoreRepository storeRepository;
    
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
        return registerRepository.findByStore_Id(storeId);
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
        return registerRepository.findByStore_IdAndStatus(storeId, status);
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

    /**
     * Create a register, resolving store from ID
     */
    public Register createWithIds(Register register, Long storeId) {
        resolveRelationships(register, storeId);
        return create(register);
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
        existing.setStore(register.getStore());
        existing.setDescription(register.getDescription());
        existing.setIsActive(register.getIsActive());
        existing.setStatus(register.getStatus());
        return registerRepository.save(existing);
    }

    /**
     * Update a register, resolving store from ID
     */
    public Register updateWithIds(Long id, Register register, Long storeId) {
        resolveRelationships(register, storeId);
        return update(id, register);
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
        return registerRepository.countByStore_Id(storeId);
    }

    private void resolveRelationships(Register register, Long storeId) {
        if (storeId != null) {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));
            register.setStore(store);
        }
    }
    
    private void validateRegister(Register register) {
        if (register == null) {
            throw new IllegalArgumentException("Register cannot be null");
        }
        if (register.getNumber() == null || register.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required");
        }
        if (register.getStore() == null) {
            throw new IllegalArgumentException("Store is required");
        }
    }
}
