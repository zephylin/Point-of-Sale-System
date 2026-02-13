package com.pos.backend.repository;

import com.pos.backend.domain.SaleLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleLineItemRepository extends JpaRepository<SaleLineItem, Long> {
    
    List<SaleLineItem> findBySaleId(Long saleId);
    
    List<SaleLineItem> findByItemId(Long itemId);
    
    @Query("SELECT sli FROM SaleLineItem sli WHERE sli.saleId IN :saleIds")
    List<SaleLineItem> findBySaleIds(@Param("saleIds") List<Long> saleIds);
    
    @Query("SELECT SUM(sli.quantity) FROM SaleLineItem sli WHERE sli.itemId = :itemId")
    Integer getTotalQuantitySoldForItem(@Param("itemId") Long itemId);
    
    @Query("SELECT sli.itemId, SUM(sli.quantity) FROM SaleLineItem sli GROUP BY sli.itemId ORDER BY SUM(sli.quantity) DESC")
    List<Object[]> getTopSellingItems();
    
    long countBySaleId(Long saleId);
    
    long countByItemId(Long itemId);
}
