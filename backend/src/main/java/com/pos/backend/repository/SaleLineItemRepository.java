package com.pos.backend.repository;

import com.pos.backend.domain.SaleLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleLineItemRepository extends JpaRepository<SaleLineItem, Long> {
    
    List<SaleLineItem> findBySale_Id(Long saleId);
    
    List<SaleLineItem> findByItem_Id(Long itemId);
    
    @Query("SELECT sli FROM SaleLineItem sli WHERE sli.sale.id IN :saleIds")
    List<SaleLineItem> findBySaleIds(@Param("saleIds") List<Long> saleIds);
    
    @Query("SELECT SUM(sli.quantity) FROM SaleLineItem sli WHERE sli.item.id = :itemId")
    Integer getTotalQuantitySoldForItem(@Param("itemId") Long itemId);
    
    @Query("SELECT sli.item.id, SUM(sli.quantity) FROM SaleLineItem sli GROUP BY sli.item.id ORDER BY SUM(sli.quantity) DESC")
    List<Object[]> getTopSellingItems();
    
    long countBySale_Id(Long saleId);
    
    long countByItem_Id(Long itemId);
}
