package com.pos.backend.repository;

import com.pos.backend.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    List<Sale> findBySession_Id(Long sessionId);
    
    List<Sale> findByStore_Id(Long storeId);
    
    List<Sale> findByCashier_Id(Long cashierId);
    
    List<Sale> findByStatus(String status);
    
    List<Sale> findByPaymentMethod(String paymentMethod);
    
    List<Sale> findByTaxFree(Boolean taxFree);
    
    @Query("SELECT s FROM Sale s WHERE s.dateTime >= :startDate AND s.dateTime < :endDate")
    List<Sale> findSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Sale s WHERE s.store.id = :storeId AND s.dateTime >= :startDate AND s.dateTime < :endDate")
    List<Sale> findSalesByStoreAndDateRange(@Param("storeId") Long storeId, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Sale s WHERE s.cashier.id = :cashierId AND s.dateTime >= :startDate AND s.dateTime < :endDate")
    List<Sale> findSalesByCashierAndDateRange(@Param("cashierId") Long cashierId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.store.id = :storeId AND s.status = 'COMPLETED'")
    BigDecimal getTotalSalesByStore(@Param("storeId") Long storeId);
    
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.session.id = :sessionId AND s.status = 'COMPLETED'")
    BigDecimal getTotalSalesBySession(@Param("sessionId") Long sessionId);
    
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.dateTime >= :startDate AND s.dateTime < :endDate AND s.status = 'COMPLETED'")
    BigDecimal getTotalSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    long countBySession_Id(Long sessionId);
    
    long countByStore_Id(Long storeId);
    
    long countByCashier_Id(Long cashierId);
    
    long countByStatus(String status);
}
