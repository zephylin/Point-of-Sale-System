package com.pos.backend.service;

import com.pos.backend.domain.Item;
import com.pos.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Item entity.
 * Contains business logic for item operations.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    
    /**
     * Get all items
     * @return List of all items
     */
    @Transactional(readOnly = true)
    public List<Item> findAll() {
        log.debug("Finding all items");
        return itemRepository.findAll();
    }
    
    /**
     * Get item by ID
     * @param id Item ID
     * @return Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        log.debug("Finding item by id: {}", id);
        return itemRepository.findById(id);
    }
    
    /**
     * Get item by item number
     * @param number Item number
     * @return Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> findByNumber(String number) {
        log.debug("Finding item by number: {}", number);
        return itemRepository.findByNumber(number);
    }
    
    /**
     * Get item by barcode
     * @param barcode Item barcode
     * @return Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> findByBarcode(String barcode) {
        log.debug("Finding item by barcode: {}", barcode);
        return itemRepository.findByBarcode(barcode);
    }
    
    /**
     * Get item by SKU
     * @param sku Item SKU
     * @return Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> findBySku(String sku) {
        log.debug("Finding item by SKU: {}", sku);
        return itemRepository.findBySku(sku);
    }
    
    /**
     * Search items by description
     * @param keyword Search keyword
     * @return List of matching items
     */
    @Transactional(readOnly = true)
    public List<Item> search(String keyword) {
        log.debug("Searching items with keyword: {}", keyword);
        return itemRepository.findByDescriptionContainingIgnoreCase(keyword);
    }
    
    /**
     * Find items by category
     * @param category Item category
     * @return List of items in the category
     */
    @Transactional(readOnly = true)
    public List<Item> findByCategory(String category) {
        log.debug("Finding items by category: {}", category);
        return itemRepository.findByCategory(category);
    }
    
    /**
     * Find items by brand
     * @param brand Item brand
     * @return List of items from the brand
     */
    @Transactional(readOnly = true)
    public List<Item> findByBrand(String brand) {
        log.debug("Finding items by brand: {}", brand);
        return itemRepository.findByBrand(brand);
    }
    
    /**
     * Find items by store
     * @param storeId Store ID
     * @return List of items in the store
     */
    @Transactional(readOnly = true)
    public List<Item> findByStore(Long storeId) {
        log.debug("Finding items by store: {}", storeId);
        return itemRepository.findByStoreId(storeId);
    }
    
    /**
     * Find items by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of items in the price range
     */
    @Transactional(readOnly = true)
    public List<Item> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Finding items with price between {} and {}", minPrice, maxPrice);
        return itemRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get all active items
     * @return List of active items
     */
    @Transactional(readOnly = true)
    public List<Item> findAllActive() {
        log.debug("Finding all active items");
        return itemRepository.findByIsActive(true);
    }
    
    /**
     * Get items needing reorder
     * @return List of items below minimum quantity
     */
    @Transactional(readOnly = true)
    public List<Item> findItemsNeedingReorder() {
        log.debug("Finding items needing reorder");
        return itemRepository.findItemsNeedingReorder();
    }
    
    /**
     * Get out of stock items
     * @return List of items with zero quantity
     */
    @Transactional(readOnly = true)
    public List<Item> findOutOfStockItems() {
        log.debug("Finding out of stock items");
        return itemRepository.findOutOfStockItems();
    }
    
    /**
     * Get low stock items
     * @param threshold Quantity threshold
     * @return List of items below threshold
     */
    @Transactional(readOnly = true)
    public List<Item> findLowStockItems(Integer threshold) {
        log.debug("Finding low stock items with threshold: {}", threshold);
        return itemRepository.findLowStockItems(threshold);
    }
    
    /**
     * Create a new item
     * @param item Item to create
     * @return Created item
     * @throws IllegalArgumentException if validation fails
     */
    public Item create(Item item) {
        log.info("Creating new item: {}", item.getNumber());
        
        // Validate
        validateItem(item);
        
        // Check for duplicates
        if (itemRepository.existsByNumber(item.getNumber())) {
            throw new IllegalArgumentException("Item with number '" + item.getNumber() + "' already exists");
        }
        
        if (item.getBarcode() != null && !item.getBarcode().isEmpty()) {
            if (itemRepository.existsByBarcode(item.getBarcode())) {
                throw new IllegalArgumentException("Item with barcode '" + item.getBarcode() + "' already exists");
            }
        }
        
        // Set defaults
        if (item.getIsActive() == null) {
            item.setIsActive(true);
        }
        if (item.getIsTaxable() == null) {
            item.setIsTaxable(true);
        }
        if (item.getQuantity() == null) {
            item.setQuantity(0);
        }
        if (item.getCreatedDate() == null) {
            item.setCreatedDate(LocalDateTime.now());
        }
        
        Item saved = itemRepository.save(item);
        log.info("Created item with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Update an existing item
     * @param id Item ID
     * @param item Updated item data
     * @return Updated item
     * @throws IllegalArgumentException if item not found or validation fails
     */
    public Item update(Long id, Item item) {
        log.info("Updating item with id: {}", id);
        
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        
        // Validate
        validateItem(item);
        
        // Check for duplicate number (excluding current record)
        Optional<Item> duplicateNumber = itemRepository.findByNumber(item.getNumber());
        if (duplicateNumber.isPresent() && !duplicateNumber.get().getId().equals(id)) {
            throw new IllegalArgumentException("Item with number '" + item.getNumber() + "' already exists");
        }
        
        // Check for duplicate barcode (excluding current record)
        if (item.getBarcode() != null && !item.getBarcode().isEmpty()) {
            Optional<Item> duplicateBarcode = itemRepository.findByBarcode(item.getBarcode());
            if (duplicateBarcode.isPresent() && !duplicateBarcode.get().getId().equals(id)) {
                throw new IllegalArgumentException("Item with barcode '" + item.getBarcode() + "' already exists");
            }
        }
        
        // Update fields
        existing.setNumber(item.getNumber());
        existing.setDescription(item.getDescription());
        existing.setPrice(item.getPrice());
        existing.setCost(item.getCost());
        existing.setQuantity(item.getQuantity());
        existing.setMinQuantity(item.getMinQuantity());
        existing.setMaxQuantity(item.getMaxQuantity());
        existing.setTaxCategoryId(item.getTaxCategoryId());
        existing.setStoreId(item.getStoreId());
        existing.setBarcode(item.getBarcode());
        existing.setSku(item.getSku());
        existing.setBrand(item.getBrand());
        existing.setCategory(item.getCategory());
        existing.setIsActive(item.getIsActive());
        existing.setIsTaxable(item.getIsTaxable());
        existing.setModifiedDate(LocalDateTime.now());
        
        Item updated = itemRepository.save(existing);
        log.info("Updated item with id: {}", id);
        return updated;
    }
    
    /**
     * Update item quantity
     * @param id Item ID
     * @param quantity New quantity
     * @return Updated item
     * @throws IllegalArgumentException if item not found or quantity is negative
     */
    public Item updateQuantity(Long id, Integer quantity) {
        log.info("Updating quantity for item id: {} to {}", id, quantity);
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        
        item.setQuantity(quantity);
        item.setModifiedDate(LocalDateTime.now());
        
        Item updated = itemRepository.save(item);
        log.info("Updated quantity for item id: {}", id);
        return updated;
    }
    
    /**
     * Adjust item quantity (add or subtract)
     * @param id Item ID
     * @param adjustment Quantity adjustment (positive or negative)
     * @return Updated item
     * @throws IllegalArgumentException if item not found or result would be negative
     */
    public Item adjustQuantity(Long id, Integer adjustment) {
        log.info("Adjusting quantity for item id: {} by {}", id, adjustment);
        
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        
        int newQuantity = item.getQuantity() + adjustment;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Adjustment would result in negative quantity");
        }
        
        item.setQuantity(newQuantity);
        item.setModifiedDate(LocalDateTime.now());
        
        Item updated = itemRepository.save(item);
        log.info("Adjusted quantity for item id: {}, new quantity: {}", id, newQuantity);
        return updated;
    }
    
    /**
     * Delete an item
     * @param id Item ID
     * @throws IllegalArgumentException if item not found
     */
    public void delete(Long id) {
        log.info("Deleting item with id: {}", id);
        
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item not found with id: " + id);
        }
        
        itemRepository.deleteById(id);
        log.info("Deleted item with id: {}", id);
    }
    
    /**
     * Deactivate an item (soft delete)
     * @param id Item ID
     * @return Deactivated item
     * @throws IllegalArgumentException if item not found
     */
    public Item deactivate(Long id) {
        log.info("Deactivating item with id: {}", id);
        
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        
        item.setIsActive(false);
        item.setModifiedDate(LocalDateTime.now());
        
        Item updated = itemRepository.save(item);
        log.info("Deactivated item with id: {}", id);
        return updated;
    }
    
    /**
     * Count all items
     * @return Total count
     */
    @Transactional(readOnly = true)
    public long count() {
        return itemRepository.count();
    }
    
    /**
     * Count active items
     * @return Count of active items
     */
    @Transactional(readOnly = true)
    public long countActive() {
        return itemRepository.countByIsActive(true);
    }
    
    /**
     * Count items by store
     * @param storeId Store ID
     * @return Count of items in the store
     */
    @Transactional(readOnly = true)
    public long countByStore(Long storeId) {
        return itemRepository.countByStoreId(storeId);
    }
    
    /**
     * Count items by category
     * @param category Item category
     * @return Count of items in the category
     */
    @Transactional(readOnly = true)
    public long countByCategory(String category) {
        return itemRepository.countByCategory(category);
    }
    
    /**
     * Validate item data
     * @param item Item to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (item.getNumber() == null || item.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Item number is required");
        }
        
        if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Item description is required");
        }
        
        if (item.getPrice() == null) {
            throw new IllegalArgumentException("Item price is required");
        }
        
        if (item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Item price cannot be negative");
        }
        
        if (item.getCost() != null && item.getCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Item cost cannot be negative");
        }
        
        if (item.getQuantity() != null && item.getQuantity() < 0) {
            throw new IllegalArgumentException("Item quantity cannot be negative");
        }
        
        if (item.getNumber().length() > 50) {
            throw new IllegalArgumentException("Item number must not exceed 50 characters");
        }
        
        if (item.getDescription().length() > 500) {
            throw new IllegalArgumentException("Item description must not exceed 500 characters");
        }
    }
}
