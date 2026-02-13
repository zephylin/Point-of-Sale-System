package com.pos.backend.service;

import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.repository.SaleLineItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SaleLineItemService {
    
    private final SaleLineItemRepository saleLineItemRepository;
    
    @Transactional(readOnly = true)
    public List<SaleLineItem> findAll() {
        return saleLineItemRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<SaleLineItem> findById(Long id) {
        return saleLineItemRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<SaleLineItem> findBySale(Long saleId) {
        return saleLineItemRepository.findBySaleId(saleId);
    }
    
    @Transactional(readOnly = true)
    public List<SaleLineItem> findByItem(Long itemId) {
        return saleLineItemRepository.findByItemId(itemId);
    }
    
    @Transactional(readOnly = true)
    public Integer getTotalQuantitySoldForItem(Long itemId) {
        Integer total = saleLineItemRepository.getTotalQuantitySoldForItem(itemId);
        return total != null ? total : 0;
    }
    
    public SaleLineItem create(SaleLineItem saleLineItem) {
        validateSaleLineItem(saleLineItem);
        
        // Calculate extended price
        if (saleLineItem.getUnitPrice() != null && saleLineItem.getQuantity() != null) {
            BigDecimal extendedPrice = saleLineItem.getUnitPrice()
                    .multiply(new BigDecimal(saleLineItem.getQuantity()));
            saleLineItem.setExtendedPrice(extendedPrice);
            
            // Calculate tax if tax rate is provided
            if (saleLineItem.getTaxRate() != null) {
                BigDecimal taxAmount = extendedPrice.multiply(saleLineItem.getTaxRate());
                saleLineItem.setTaxAmount(taxAmount);
                saleLineItem.setTotalPrice(extendedPrice.add(taxAmount));
            } else {
                saleLineItem.setTaxAmount(BigDecimal.ZERO);
                saleLineItem.setTotalPrice(extendedPrice);
            }
        }
        
        return saleLineItemRepository.save(saleLineItem);
    }
    
    public SaleLineItem update(Long id, SaleLineItem saleLineItem) {
        SaleLineItem existing = saleLineItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale line item not found with id: " + id));
        validateSaleLineItem(saleLineItem);
        
        existing.setSaleId(saleLineItem.getSaleId());
        existing.setItemId(saleLineItem.getItemId());
        existing.setQuantity(saleLineItem.getQuantity());
        existing.setUnitPrice(saleLineItem.getUnitPrice());
        
        // Recalculate prices
        BigDecimal extendedPrice = saleLineItem.getUnitPrice()
                .multiply(new BigDecimal(saleLineItem.getQuantity()));
        existing.setExtendedPrice(extendedPrice);
        
        if (saleLineItem.getTaxRate() != null) {
            BigDecimal taxAmount = extendedPrice.multiply(saleLineItem.getTaxRate());
            existing.setTaxAmount(taxAmount);
            existing.setTotalPrice(extendedPrice.add(taxAmount));
        } else {
            existing.setTaxAmount(BigDecimal.ZERO);
            existing.setTotalPrice(extendedPrice);
        }
        
        existing.setDiscount(saleLineItem.getDiscount());
        existing.setNotes(saleLineItem.getNotes());
        
        return saleLineItemRepository.save(existing);
    }
    
    public void delete(Long id) {
        if (!saleLineItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Sale line item not found with id: " + id);
        }
        saleLineItemRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return saleLineItemRepository.count();
    }
    
    private void validateSaleLineItem(SaleLineItem saleLineItem) {
        if (saleLineItem == null) {
            throw new IllegalArgumentException("Sale line item cannot be null");
        }
        if (saleLineItem.getSaleId() == null) {
            throw new IllegalArgumentException("Sale ID is required");
        }
        if (saleLineItem.getItemId() == null) {
            throw new IllegalArgumentException("Item ID is required");
        }
        if (saleLineItem.getQuantity() == null || saleLineItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (saleLineItem.getUnitPrice() == null || saleLineItem.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
    }
}
