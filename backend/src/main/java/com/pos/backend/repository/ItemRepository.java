package com.pos.backend.repository;

import com.pos.backend.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find item by item number
     * @param number Item number
     * @return Optional containing the item if found
     */
    Optional<Item> findByNumber(String number);
    
    /**
     * Find item by barcode
     * @param barcode Item barcode
     * @return Optional containing the item if found
     */
    Optional<Item> findByBarcode(String barcode);
    
    /**
     * Find item by SKU
     * @param sku Item SKU
     * @return Optional containing the item if found
     */
    Optional<Item> findBySku(String sku);
    
    /**
     * Find items by description containing keyword (case-insensitive)
     * @param keyword Search keyword
     * @return List of matching items
     */
    List<Item> findByDescriptionContainingIgnoreCase(String keyword);
    
    /**
     * Find items by category
     * @param category Item category
     * @return List of items in the category
     */
    List<Item> findByCategory(String category);
    
    /**
     * Find items by brand
     * @param brand Item brand
     * @return List of items from the brand
     */
    List<Item> findByBrand(String brand);
    
    /**
     * Find items by store
     * @param storeId Store ID
     * @return List of items in the store
     */
    List<Item> findByStoreId(Long storeId);
    
    /**
     * Find items by tax category
     * @param taxCategoryId Tax category ID
     * @return List of items in the tax category
     */
    List<Item> findByTaxCategoryId(Long taxCategoryId);
    
    /**
     * Find all active items
     * @param isActive Active status
     * @return List of active items
     */
    List<Item> findByIsActive(Boolean isActive);
    
    /**
     * Find all taxable items
     * @param isTaxable Taxable status
     * @return List of taxable items
     */
    List<Item> findByIsTaxable(Boolean isTaxable);
    
    /**
     * Find items with price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of items in the price range
     */
    List<Item> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find items that need reordering
     * @return List of items below minimum quantity
     */
    @Query("SELECT i FROM Item i WHERE i.quantity <= i.minQuantity AND i.minQuantity IS NOT NULL")
    List<Item> findItemsNeedingReorder();
    
    /**
     * Find out of stock items
     * @return List of items with quantity = 0
     */
    @Query("SELECT i FROM Item i WHERE i.quantity = 0")
    List<Item> findOutOfStockItems();
    
    /**
     * Find low stock items
     * @param threshold Quantity threshold
     * @return List of items below threshold
     */
    @Query("SELECT i FROM Item i WHERE i.quantity <= :threshold")
    List<Item> findLowStockItems(@Param("threshold") Integer threshold);
    
    /**
     * Check if item with number exists
     * @param number Item number
     * @return True if exists
     */
    boolean existsByNumber(String number);
    
    /**
     * Check if item with barcode exists
     * @param barcode Item barcode
     * @return True if exists
     */
    boolean existsByBarcode(String barcode);
    
    /**
     * Count items by store
     * @param storeId Store ID
     * @return Count of items
     */
    long countByStoreId(Long storeId);
    
    /**
     * Count items by category
     * @param category Item category
     * @return Count of items
     */
    long countByCategory(String category);
    
    /**
     * Count active items
     * @param isActive Active status
     * @return Count of active items
     */
    long countByIsActive(Boolean isActive);
}
