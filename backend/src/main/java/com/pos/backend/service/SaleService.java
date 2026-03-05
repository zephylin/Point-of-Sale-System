package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Sale;
import com.pos.backend.domain.Session;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.SaleRepository;
import com.pos.backend.repository.SessionRepository;
import com.pos.backend.repository.StoreRepository;
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
    private final SessionRepository sessionRepository;
    private final StoreRepository storeRepository;
    private final CashierRepository cashierRepository;
    
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
        return saleRepository.findBySession_Id(sessionId);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByStore(Long storeId) {
        return saleRepository.findByStore_Id(storeId);
    }
    
    @Transactional(readOnly = true)
    public List<Sale> findByCashier(Long cashierId) {
        return saleRepository.findByCashier_Id(cashierId);
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

    /**
     * Create a sale, resolving session, store, and cashier from IDs
     */
    public Sale createWithIds(Sale sale, Long sessionId, Long storeId, Long cashierId) {
        resolveRelationships(sale, sessionId, storeId, cashierId);
        return create(sale);
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

    /**
     * Update a sale, resolving session, store, and cashier from IDs
     */
    public Sale updateWithIds(Long id, Sale sale, Long sessionId, Long storeId, Long cashierId) {
        resolveRelationships(sale, sessionId, storeId, cashierId);
        return update(id, sale);
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
        return saleRepository.countByStore_Id(storeId);
    }

    private void resolveRelationships(Sale sale, Long sessionId, Long storeId, Long cashierId) {
        if (sessionId != null) {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
            sale.setSession(session);
        }
        if (storeId != null) {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));
            sale.setStore(store);
        }
        if (cashierId != null) {
            Cashier cashier = cashierRepository.findById(cashierId)
                    .orElseThrow(() -> new IllegalArgumentException("Cashier not found with id: " + cashierId));
            sale.setCashier(cashier);
        }
    }
    
    private void validateSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }
        if (sale.getSession() == null) {
            throw new IllegalArgumentException("Session is required");
        }
        if (sale.getStore() == null) {
            throw new IllegalArgumentException("Store is required");
        }
        if (sale.getCashier() == null) {
            throw new IllegalArgumentException("Cashier is required");
        }
    }
}
