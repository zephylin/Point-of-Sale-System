package com.pos.backend.controller;

import com.pos.backend.domain.Item;
import com.pos.backend.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Item operations.
 * Provides endpoints for CRUD operations on items.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Items", description = "Item management APIs")
public class ItemController {
    
    private final ItemService itemService;
    
    @Operation(summary = "Get all items", description = "Retrieve a list of all items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        log.debug("GET /api/items - Get all items");
        List<Item> items = itemService.findAll();
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get item by ID", description = "Retrieve a specific item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(
            @Parameter(description = "Item ID") @PathVariable Long id) {
        log.debug("GET /api/items/{} - Get item by id", id);
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get item by number", description = "Retrieve an item by its item number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/number/{number}")
    public ResponseEntity<Item> getItemByNumber(
            @Parameter(description = "Item number") @PathVariable String number) {
        log.debug("GET /api/items/number/{} - Get item by number", number);
        return itemService.findByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get item by barcode", description = "Retrieve an item by its barcode")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Item> getItemByBarcode(
            @Parameter(description = "Item barcode") @PathVariable String barcode) {
        log.debug("GET /api/items/barcode/{} - Get item by barcode", barcode);
        return itemService.findByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get item by SKU", description = "Retrieve an item by its SKU")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<Item> getItemBySku(
            @Parameter(description = "Item SKU") @PathVariable String sku) {
        log.debug("GET /api/items/sku/{} - Get item by SKU", sku);
        return itemService.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Search items", description = "Search items by description keyword")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.debug("GET /api/items/search?keyword={} - Search items", keyword);
        List<Item> results = itemService.search(keyword);
        return ResponseEntity.ok(results);
    }
    
    @Operation(summary = "Get items by category", description = "Retrieve all items in a specific category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getItemsByCategory(
            @Parameter(description = "Item category") @PathVariable String category) {
        log.debug("GET /api/items/category/{} - Get items by category", category);
        List<Item> items = itemService.findByCategory(category);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get items by brand", description = "Retrieve all items of a specific brand")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Item>> getItemsByBrand(
            @Parameter(description = "Item brand") @PathVariable String brand) {
        log.debug("GET /api/items/brand/{} - Get items by brand", brand);
        List<Item> items = itemService.findByBrand(brand);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get items by store", description = "Retrieve all items in a specific store")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Item>> getItemsByStore(
            @Parameter(description = "Store ID") @PathVariable Long storeId) {
        log.debug("GET /api/items/store/{} - Get items by store", storeId);
        List<Item> items = itemService.findByStore(storeId);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get items by price range", description = "Retrieve items within a price range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping("/price-range")
    public ResponseEntity<List<Item>> getItemsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice) {
        log.debug("GET /api/items/price-range?minPrice={}&maxPrice={} - Get items by price range", minPrice, maxPrice);
        List<Item> items = itemService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get active items", description = "Retrieve all active items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active items")
    @GetMapping("/active")
    public ResponseEntity<List<Item>> getActiveItems() {
        log.debug("GET /api/items/active - Get active items");
        List<Item> items = itemService.findAllActive();
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get items needing reorder", description = "Retrieve items below minimum quantity")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping("/reorder")
    public ResponseEntity<List<Item>> getItemsNeedingReorder() {
        log.debug("GET /api/items/reorder - Get items needing reorder");
        List<Item> items = itemService.findItemsNeedingReorder();
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get out of stock items", description = "Retrieve items with zero quantity")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved out of stock items")
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<Item>> getOutOfStockItems() {
        log.debug("GET /api/items/out-of-stock - Get out of stock items");
        List<Item> items = itemService.findOutOfStockItems();
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get low stock items", description = "Retrieve items below a quantity threshold")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock items")
    @GetMapping("/low-stock")
    public ResponseEntity<List<Item>> getLowStockItems(
            @Parameter(description = "Quantity threshold") @RequestParam Integer threshold) {
        log.debug("GET /api/items/low-stock?threshold={} - Get low stock items", threshold);
        List<Item> items = itemService.findLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Create item", description = "Create a new item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate item")
    })
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        log.debug("POST /api/items - Create item: {}", item.getNumber());
        try {
            Item created = itemService.create(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update item", description = "Update an existing item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item updated successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @Parameter(description = "Item ID") @PathVariable Long id,
            @RequestBody Item item) {
        log.debug("PUT /api/items/{} - Update item", id);
        try {
            Item updated = itemService.update(id, item);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update item quantity", description = "Update the quantity of an item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity")
    })
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<?> updateItemQuantity(
            @Parameter(description = "Item ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        log.debug("PATCH /api/items/{}/quantity - Update item quantity", id);
        try {
            Integer quantity = request.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().body("Quantity is required");
            }
            Item updated = itemService.updateQuantity(id, quantity);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating item quantity: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Adjust item quantity", description = "Adjust the quantity of an item (add or subtract)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantity adjusted successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid adjustment")
    })
    @PatchMapping("/{id}/adjust-quantity")
    public ResponseEntity<?> adjustItemQuantity(
            @Parameter(description = "Item ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        log.debug("PATCH /api/items/{}/adjust-quantity - Adjust item quantity", id);
        try {
            Integer adjustment = request.get("adjustment");
            if (adjustment == null) {
                return ResponseEntity.badRequest().body("Adjustment is required");
            }
            Item updated = itemService.adjustQuantity(id, adjustment);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error adjusting item quantity: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete item", description = "Delete an item by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            @Parameter(description = "Item ID") @PathVariable Long id) {
        log.debug("DELETE /api/items/{} - Delete item", id);
        try {
            itemService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Deactivate item", description = "Deactivate an item (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateItem(
            @Parameter(description = "Item ID") @PathVariable Long id) {
        log.debug("PATCH /api/items/{}/deactivate - Deactivate item", id);
        try {
            Item deactivated = itemService.deactivate(id);
            return ResponseEntity.ok(deactivated);
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count items", description = "Get the total count of items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count")
    public ResponseEntity<Long> countItems() {
        log.debug("GET /api/items/count - Count items");
        long count = itemService.count();
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count active items", description = "Get the count of active items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveItems() {
        log.debug("GET /api/items/count/active - Count active items");
        long count = itemService.countActive();
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count items by store", description = "Get the count of items in a specific store")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count/store/{storeId}")
    public ResponseEntity<Long> countItemsByStore(
            @Parameter(description = "Store ID") @PathVariable Long storeId) {
        log.debug("GET /api/items/count/store/{} - Count items by store", storeId);
        long count = itemService.countByStore(storeId);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count items by category", description = "Get the count of items in a specific category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count/category/{category}")
    public ResponseEntity<Long> countItemsByCategory(
            @Parameter(description = "Category name") @PathVariable String category) {
        log.debug("GET /api/items/count/category/{} - Count items by category", category);
        long count = itemService.countByCategory(category);
        return ResponseEntity.ok(count);
    }
}
