package com.pos.backend.controller;

import com.pos.backend.domain.TaxCategory;
import com.pos.backend.service.TaxCategoryService;
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

import java.util.List;

/**
 * REST Controller for TaxCategory operations.
 * Provides endpoints for CRUD operations on tax categories.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@RestController
@RequestMapping("/api/tax-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tax Categories", description = "Tax Category management APIs")
public class TaxCategoryController {
    
    private final TaxCategoryService taxCategoryService;
    
    @Operation(summary = "Get all tax categories", description = "Retrieve a list of all tax categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public ResponseEntity<List<TaxCategory>> getAllTaxCategories() {
        log.debug("GET /api/tax-categories - Get all tax categories");
        List<TaxCategory> taxCategories = taxCategoryService.findAll();
        return ResponseEntity.ok(taxCategories);
    }
    
    @Operation(summary = "Get tax category by ID", description = "Retrieve a specific tax category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tax category"),
        @ApiResponse(responseCode = "404", description = "Tax category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaxCategory> getTaxCategoryById(
            @Parameter(description = "Tax category ID") @PathVariable Long id) {
        log.debug("GET /api/tax-categories/{} - Get tax category by id", id);
        return taxCategoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get tax category by name", description = "Retrieve a tax category by its category name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tax category"),
        @ApiResponse(responseCode = "404", description = "Tax category not found")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<TaxCategory> getTaxCategoryByName(
            @Parameter(description = "Category name") @PathVariable String category) {
        log.debug("GET /api/tax-categories/category/{} - Get tax category by name", category);
        return taxCategoryService.findByCategory(category)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Search tax categories", description = "Search tax categories by keyword")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    @GetMapping("/search")
    public ResponseEntity<List<TaxCategory>> searchTaxCategories(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.debug("GET /api/tax-categories/search?keyword={} - Search tax categories", keyword);
        List<TaxCategory> results = taxCategoryService.search(keyword);
        return ResponseEntity.ok(results);
    }
    
    @Operation(summary = "Get active tax categories", description = "Retrieve all active tax categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active tax categories")
    @GetMapping("/active")
    public ResponseEntity<List<TaxCategory>> getActiveTaxCategories() {
        log.debug("GET /api/tax-categories/active - Get active tax categories");
        List<TaxCategory> taxCategories = taxCategoryService.findAllActive();
        return ResponseEntity.ok(taxCategories);
    }
    
    @Operation(summary = "Create tax category", description = "Create a new tax category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tax category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate category")
    })
    @PostMapping
    public ResponseEntity<?> createTaxCategory(@RequestBody TaxCategory taxCategory) {
        log.debug("POST /api/tax-categories - Create tax category: {}", taxCategory.getCategory());
        try {
            TaxCategory created = taxCategoryService.create(taxCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating tax category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update tax category", description = "Update an existing tax category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tax category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Tax category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaxCategory(
            @Parameter(description = "Tax category ID") @PathVariable Long id,
            @RequestBody TaxCategory taxCategory) {
        log.debug("PUT /api/tax-categories/{} - Update tax category", id);
        try {
            TaxCategory updated = taxCategoryService.update(id, taxCategory);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating tax category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete tax category", description = "Delete a tax category by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tax category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Tax category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaxCategory(
            @Parameter(description = "Tax category ID") @PathVariable Long id) {
        log.debug("DELETE /api/tax-categories/{} - Delete tax category", id);
        try {
            taxCategoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting tax category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Deactivate tax category", description = "Deactivate a tax category (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tax category deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Tax category not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateTaxCategory(
            @Parameter(description = "Tax category ID") @PathVariable Long id) {
        log.debug("PATCH /api/tax-categories/{}/deactivate - Deactivate tax category", id);
        try {
            TaxCategory deactivated = taxCategoryService.deactivate(id);
            return ResponseEntity.ok(deactivated);
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating tax category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count tax categories", description = "Get the total count of tax categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count")
    public ResponseEntity<Long> countTaxCategories() {
        log.debug("GET /api/tax-categories/count - Count tax categories");
        long count = taxCategoryService.count();
        return ResponseEntity.ok(count);
    }
}
