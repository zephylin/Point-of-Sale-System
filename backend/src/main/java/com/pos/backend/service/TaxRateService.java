package com.pos.backend.service;

import com.pos.backend.domain.TaxRate;
import com.pos.backend.repository.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaxRateService {
    
    private final TaxRateRepository taxRateRepository;
    
    @Transactional(readOnly = true)
    public List<TaxRate> findAll() {
        return taxRateRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<TaxRate> findById(Long id) {
        return taxRateRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<TaxRate> findByTaxCategory(Long taxCategoryId) {
        return taxRateRepository.findByTaxCategoryId(taxCategoryId);
    }
    
    @Transactional(readOnly = true)
    public List<TaxRate> findAllActive() {
        return taxRateRepository.findByIsActive(true);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaxRate> getCurrentRateForCategory(Long taxCategoryId) {
        return taxRateRepository.findTopByTaxCategoryIdAndEffectiveDateLessThanEqualAndIsActiveTrueOrderByEffectiveDateDesc(
                taxCategoryId, LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public Optional<TaxRate> getRateForCategoryAndDate(Long taxCategoryId, LocalDate date) {
        return taxRateRepository.findTopByTaxCategoryIdAndEffectiveDateLessThanEqualAndIsActiveTrueOrderByEffectiveDateDesc(
                taxCategoryId, date);
    }
    
    public TaxRate create(TaxRate taxRate) {
        validateTaxRate(taxRate);
        if (taxRate.getIsActive() == null) {
            taxRate.setIsActive(true);
        }
        return taxRateRepository.save(taxRate);
    }
    
    public TaxRate update(Long id, TaxRate taxRate) {
        TaxRate existing = taxRateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax rate not found with id: " + id));
        validateTaxRate(taxRate);
        existing.setRate(taxRate.getRate());
        existing.setEffectiveDate(taxRate.getEffectiveDate());
        existing.setTaxCategoryId(taxRate.getTaxCategoryId());
        existing.setDescription(taxRate.getDescription());
        existing.setIsActive(taxRate.getIsActive());
        return taxRateRepository.save(existing);
    }
    
    public void delete(Long id) {
        if (!taxRateRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax rate not found with id: " + id);
        }
        taxRateRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return taxRateRepository.count();
    }
    
    private void validateTaxRate(TaxRate taxRate) {
        if (taxRate == null) {
            throw new IllegalArgumentException("Tax rate cannot be null");
        }
        if (taxRate.getRate() == null) {
            throw new IllegalArgumentException("Rate is required");
        }
        if (taxRate.getRate().compareTo(BigDecimal.ZERO) < 0 || taxRate.getRate().compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }
        if (taxRate.getEffectiveDate() == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        if (taxRate.getTaxCategoryId() == null) {
            throw new IllegalArgumentException("Tax category ID is required");
        }
    }
}
