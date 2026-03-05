package com.pos.backend.repository;

import com.pos.backend.domain.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {
    
    Optional<Register> findByNumber(String number);
    
    List<Register> findByStore_Id(Long storeId);
    
    List<Register> findByIsActive(Boolean isActive);
    
    List<Register> findByStore_IdAndIsActive(Long storeId, Boolean isActive);
    
    List<Register> findByStatus(String status);
    
    List<Register> findByStore_IdAndStatus(Long storeId, String status);
    
    boolean existsByNumber(String number);
    
    long countByStore_Id(Long storeId);
    
    long countByStatus(String status);
}
