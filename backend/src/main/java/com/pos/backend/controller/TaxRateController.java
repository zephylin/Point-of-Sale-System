package com.pos.backend.controller;

import com.pos.backend.domain.TaxRate;
import com.pos.backend.dto.TaxRateDTO;
import com.pos.backend.mapper.TaxRateMapper;
import com.pos.backend.service.TaxRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tax-rates")
@RequiredArgsConstructor
@Tag(name = "Tax Rates", description = "Tax Rate management APIs")
public class TaxRateController {
    
    private final TaxRateService taxRateService;
    private final TaxRateMapper taxRateMapper;
    
    @Operation(summary = "Get all tax rates")
    @GetMapping
    public ResponseEntity<List<TaxRateDTO.Response>> getAllTaxRates() {
        List<TaxRateDTO.Response> taxRates = taxRateService.findAll().stream()
                .map(taxRateMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taxRates);
    }
    
    @Operation(summary = "Get tax rate by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaxRateDTO.Response> getTaxRateById(@PathVariable Long id) {
        return taxRateService.findById(id)
                .map(taxRateMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get tax rates by category")
    @GetMapping("/category/{taxCategoryId}")
    public ResponseEntity<List<TaxRateDTO.Response>> getTaxRatesByCategory(@PathVariable Long taxCategoryId) {
        List<TaxRateDTO.Response> taxRates = taxRateService.findByTaxCategory(taxCategoryId).stream()
                .map(taxRateMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taxRates);
    }
    
    @Operation(summary = "Get current rate for category")
    @GetMapping("/category/{taxCategoryId}/current")
    public ResponseEntity<TaxRateDTO.Response> getCurrentRateForCategory(@PathVariable Long taxCategoryId) {
        return taxRateService.getCurrentRateForCategory(taxCategoryId)
                .map(taxRateMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get rate for category and date")
    @GetMapping("/category/{taxCategoryId}/date/{date}")
    public ResponseEntity<TaxRateDTO.Response> getRateForDate(@PathVariable Long taxCategoryId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return taxRateService.getRateForCategoryAndDate(taxCategoryId, localDate)
                .map(taxRateMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get active tax rates")
    @GetMapping("/active")
    public ResponseEntity<List<TaxRateDTO.Response>> getActiveTaxRates() {
        List<TaxRateDTO.Response> taxRates = taxRateService.findAllActive().stream()
                .map(taxRateMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taxRates);
    }
    
    @Operation(summary = "Create tax rate")
    @PostMapping
    public ResponseEntity<?> createTaxRate(@Valid @RequestBody TaxRateDTO.Request request) {
        TaxRate taxRate = taxRateMapper.toEntity(request);
        TaxRate created = taxRateService.create(taxRate);
        return ResponseEntity.status(HttpStatus.CREATED).body(taxRateMapper.toResponse(created));
    }
    
    @Operation(summary = "Update tax rate")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaxRate(@PathVariable Long id, @Valid @RequestBody TaxRateDTO.Request request) {
        TaxRate taxRate = taxRateMapper.toEntity(request);
        TaxRate updated = taxRateService.update(id, taxRate);
        return ResponseEntity.ok(taxRateMapper.toResponse(updated));
    }
    
    @Operation(summary = "Delete tax rate")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaxRate(@PathVariable Long id) {
        taxRateService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Count tax rates")
    @GetMapping("/count")
    public ResponseEntity<Long> countTaxRates() {
        return ResponseEntity.ok(taxRateService.count());
    }
}
