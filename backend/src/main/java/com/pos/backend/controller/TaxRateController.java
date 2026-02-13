package com.pos.backend.controller;

import com.pos.backend.domain.TaxRate;
import com.pos.backend.service.TaxRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tax-rates")
@RequiredArgsConstructor
@Tag(name = "Tax Rates", description = "Tax Rate management APIs")
public class TaxRateController {
    
    private final TaxRateService taxRateService;
    
    @Operation(summary = "Get all tax rates")
    @GetMapping
    public ResponseEntity<List<TaxRate>> getAllTaxRates() {
        return ResponseEntity.ok(taxRateService.findAll());
    }
    
    @Operation(summary = "Get tax rate by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaxRate> getTaxRateById(@PathVariable Long id) {
        return taxRateService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get tax rates by category")
    @GetMapping("/category/{taxCategoryId}")
    public ResponseEntity<List<TaxRate>> getTaxRatesByCategory(@PathVariable Long taxCategoryId) {
        return ResponseEntity.ok(taxRateService.findByTaxCategory(taxCategoryId));
    }
    
    @Operation(summary = "Get current rate for category")
    @GetMapping("/category/{taxCategoryId}/current")
    public ResponseEntity<TaxRate> getCurrentRateForCategory(@PathVariable Long taxCategoryId) {
        return taxRateService.getCurrentRateForCategory(taxCategoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get rate for category and date")
    @GetMapping("/category/{taxCategoryId}/date/{date}")
    public ResponseEntity<TaxRate> getRateForDate(@PathVariable Long taxCategoryId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return taxRateService.getRateForCategoryAndDate(taxCategoryId, localDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get active tax rates")
    @GetMapping("/active")
    public ResponseEntity<List<TaxRate>> getActiveTaxRates() {
        return ResponseEntity.ok(taxRateService.findAllActive());
    }
    
    @Operation(summary = "Create tax rate")
    @PostMapping
    public ResponseEntity<?> createTaxRate(@RequestBody TaxRate taxRate) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taxRateService.create(taxRate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update tax rate")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaxRate(@PathVariable Long id, @RequestBody TaxRate taxRate) {
        try {
            return ResponseEntity.ok(taxRateService.update(id, taxRate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete tax rate")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaxRate(@PathVariable Long id) {
        try {
            taxRateService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count tax rates")
    @GetMapping("/count")
    public ResponseEntity<Long> countTaxRates() {
        return ResponseEntity.ok(taxRateService.count());
    }
}
