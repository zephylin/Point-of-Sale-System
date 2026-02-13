package com.pos.backend.service;

import com.pos.backend.domain.Sale;
import com.pos.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SaleService {
    
    private final SaleRepository saleRepository;
    
    @Transactional(readOnly = true)
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Sale> findById(Long id) {
        return saleRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findBySession(Long sessionId) {
        return saleRepository.findBySessionId(sessionId);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByStore(Long storeId) {
        return saleRepository.findByStoreId(storeId);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByCashier(Long cashierId) {
        return saleRepository.findByCashierId(cashierId);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByStatus(String status) {
        return saleRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findSalesByDateRange(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesByStore(Long storeId) {
        BigDecimal total = saleRepository.getTotalSalesByStore(storeId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesBySession(Long sessionId) {
        BigDecimal total = saleRepository.getTotalSalesBySession(sessionId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public Sale create(Sale sale) {
        validateSale(sale);
        if (sale.getDateTime() == null) {
            sale.setDateTime(LocalDateTime.now());
        }
        if (sale.getStatus() == null) {
            sale.setStatus("PENDING");
        }
        if (sale.getTaxFree() == null) {
            sale.setTaxFree(false);
        }
        return saleRepository.save(sale);
    }
    
    public Sale update(Long id, Sale sale) {
        Sale existing = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with id: " + id));
        validateSale(sale);
        existing.setSubtotal(sale.getSubtotal());
        existing.setTax(sale.getTax());
        existing.setTotal(sale.getTotal());
        existing.setAmountPaid(sale.getAmountPaid());
        existing.setChange(sale.getChange());
        existing.setTaxFree(sale.getTaxFree());
        existing.setStatus(sale.getStatus());
        existing.setPaymentMethod(sale.getPaymentMethod());
        existing.setNotes(sale.getNotes());
        return saleRepository.save(existing);
    }
    
    public Sale completeSale(Long id, BigDecimal amountPaid, String paymentMethod) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with id: " + id));
        
        if (!"PENDING".equals(sale.getStatus())) {
            throw new IllegalArgumentException("Only pending sales can be completed");
        }
        
        sale.setAmountPaid(amountPaid);
        sale.setPaymentMethod(paymentMethod);
        sale.setChange(amountPaid.subtract(sale.getTotal()));
        sale.setStatus("COMPLETED");
        
        return saleRepository.save(sale);
    }
    
    public Sale voidSale(Long id, String reason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with id: " + id));
        sale.setStatus("VOID");
        sale.setNotes(reason);
        return saleRepository.save(sale);
    }
    
    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new IllegalArgumentException("Sale not found with id: " + id);
        }
        saleRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return saleRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countByStore(Long storeId) {
        return saleRepository.countByStoreId(storeId);
    }
    
    private void validateSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }
        if (sale.getSessionId() == null) {
            throw new IllegalArgumentException("Session ID is required");
        }
        if (sale.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required");
        }
        if (sale.getCashierId() == null) {
            throw new IllegalArgumentException("Cashier ID is required");
        }
    }
}
