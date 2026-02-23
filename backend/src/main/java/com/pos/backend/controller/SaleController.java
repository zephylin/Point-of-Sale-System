package com.pos.backend.controller;

import com.pos.backend.domain.Sale;
import com.pos.backend.dto.SaleDTO;
import com.pos.backend.mapper.SaleMapper;
import com.pos.backend.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales transaction APIs")
public class SaleController {
    
    private final SaleService saleService;
    private final SaleMapper saleMapper;
    
    @Operation(summary = "Get all sales")
    @GetMapping
    public ResponseEntity<List<SaleDTO.Response>> getAllSales() {
        List<SaleDTO.Response> sales = saleService.findAll().stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get sale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO.Response> getSaleById(@PathVariable Long id) {
        return saleService.findById(id)
                .map(saleMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sales by session")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SaleDTO.Response>> getSalesBySession(@PathVariable Long sessionId) {
        List<SaleDTO.Response> sales = saleService.findBySession(sessionId).stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get sales by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByStore(@PathVariable Long storeId) {
        List<SaleDTO.Response> sales = saleService.findByStore(storeId).stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get sales by cashier")
    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByCashier(@PathVariable Long cashierId) {
        List<SaleDTO.Response> sales = saleService.findByCashier(cashierId).stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get sales by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByStatus(@PathVariable String status) {
        List<SaleDTO.Response> sales = saleService.findByStatus(status).stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get sales by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<SaleDTO.Response> sales = saleService.findByDateRange(start, end).stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }
    
    @Operation(summary = "Get total sales by store")
    @GetMapping("/total/store/{storeId}")
    public ResponseEntity<BigDecimal> getTotalSalesByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(saleService.getTotalSalesByStore(storeId));
    }
    
    @Operation(summary = "Get total sales by session")
    @GetMapping("/total/session/{sessionId}")
    public ResponseEntity<BigDecimal> getTotalSalesBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(saleService.getTotalSalesBySession(sessionId));
    }
    
    @Operation(summary = "Create sale")
    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody SaleDTO.Request request) {
        try {
            Sale sale = saleMapper.toEntity(request);
            Sale created = saleService.create(sale);
            return ResponseEntity.status(HttpStatus.CREATED).body(saleMapper.toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update sale")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody SaleDTO.Request request) {
        try {
            Sale sale = saleMapper.toEntity(request);
            Sale updated = saleService.update(id, sale);
            return ResponseEntity.ok(saleMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Complete sale")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeSale(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            BigDecimal amountPaid = new BigDecimal(request.get("amountPaid"));
            String paymentMethod = request.get("paymentMethod");
            Sale completed = saleService.completeSale(id, amountPaid, paymentMethod);
            return ResponseEntity.ok(saleMapper.toResponse(completed));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Void sale")
    @PatchMapping("/{id}/void")
    public ResponseEntity<?> voidSale(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            Sale voided = saleService.voidSale(id, reason);
            return ResponseEntity.ok(saleMapper.toResponse(voided));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete sale")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        try {
            saleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count sales")
    @GetMapping("/count")
    public ResponseEntity<Long> countSales() {
        return ResponseEntity.ok(saleService.count());
    }
    
    @Operation(summary = "Count sales by store")
    @GetMapping("/count/store/{storeId}")
    public ResponseEntity<Long> countSalesByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(saleService.countByStore(storeId));
    }
}
