package com.pos.backend.controller;

import com.pos.backend.domain.Sale;
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

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales transaction APIs")
public class SaleController {
    
    private final SaleService saleService;
    
    @Operation(summary = "Get all sales")
    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.findAll());
    }
    
    @Operation(summary = "Get sale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return saleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sales by session")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<Sale>> getSalesBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(saleService.findBySession(sessionId));
    }
    
    @Operation(summary = "Get sales by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Sale>> getSalesByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(saleService.findByStore(storeId));
    }
    
    @Operation(summary = "Get sales by cashier")
    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<Sale>> getSalesByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(saleService.findByCashier(cashierId));
    }
    
    @Operation(summary = "Get sales by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Sale>> getSalesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(saleService.findByStatus(status));
    }
    
    @Operation(summary = "Get sales by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<Sale>> getSalesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return ResponseEntity.ok(saleService.findByDateRange(start, end));
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
    public ResponseEntity<?> createSale(@RequestBody Sale sale) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update sale")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody Sale sale) {
        try {
            return ResponseEntity.ok(saleService.update(id, sale));
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
            return ResponseEntity.ok(saleService.completeSale(id, amountPaid, paymentMethod));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Void sale")
    @PatchMapping("/{id}/void")
    public ResponseEntity<?> voidSale(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            return ResponseEntity.ok(saleService.voidSale(id, reason));
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
