package com.pos.backend.repository;

import com.pos.backend.domain.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
    
    List<TaxRate> findByTaxCategoryId(Long taxCategoryId);
    
    List<TaxRate> findByIsActive(Boolean isActive);
    
    List<TaxRate> findByTaxCategoryIdAndIsActive(Long taxCategoryId, Boolean isActive);
    
    @Query("SELECT t FROM TaxRate t WHERE t.taxCategoryId = :taxCategoryId AND t.effectiveDate <= :date AND t.isActive = true ORDER BY t.effectiveDate DESC")
    List<TaxRate> findEffectiveRatesForDate(@Param("taxCategoryId") Long taxCategoryId, @Param("date") LocalDate date);
    
    Optional<TaxRate> findTopByTaxCategoryIdAndEffectiveDateLessThanEqualAndIsActiveTrueOrderByEffectiveDateDesc(
            Long taxCategoryId, LocalDate date);
    
    long countByTaxCategoryId(Long taxCategoryId);
}
