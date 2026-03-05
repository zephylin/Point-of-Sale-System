package com.pos.backend.repository;

import com.pos.backend.domain.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashierRepository extends JpaRepository<Cashier, Long> {
    
    Optional<Cashier> findByNumber(String number);
    
    Optional<Cashier> findByPerson_Id(Long personId);
    
    List<Cashier> findByStore_Id(Long storeId);
    
    List<Cashier> findByIsActive(Boolean isActive);
    
    List<Cashier> findByStore_IdAndIsActive(Long storeId, Boolean isActive);
    
    List<Cashier> findByRole(String role);
    
    boolean existsByNumber(String number);
    
    boolean existsByPerson_Id(Long personId);
    
    long countByStore_Id(Long storeId);
    
    long countByIsActive(Boolean isActive);
}
