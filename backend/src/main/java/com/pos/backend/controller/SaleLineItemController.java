package com.pos.backend.controller;

import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.service.SaleLineItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-line-items")
@RequiredArgsConstructor
@Tag(name = "Sale Line Items", description = "Sale line item management APIs")
public class SaleLineItemController {
    
    private final SaleLineItemService saleLineItemService;
    
    @Operation(summary = "Get all sale line items")
    @GetMapping
    public ResponseEntity<List<SaleLineItem>> getAllSaleLineItems() {
        return ResponseEntity.ok(saleLineItemService.findAll());
    }
    
    @Operation(summary = "Get sale line item by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SaleLineItem> getSaleLineItemById(@PathVariable Long id) {
        return saleLineItemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sale line items by sale")
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleLineItem>> getSaleLineItemsBySale(@PathVariable Long saleId) {
        return ResponseEntity.ok(saleLineItemService.findBySale(saleId));
    }
    
    @Operation(summary = "Get sale line items by item")
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<SaleLineItem>> getSaleLineItemsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(saleLineItemService.findByItem(itemId));
    }
    
    @Operation(summary = "Get total quantity sold for item")
    @GetMapping("/item/{itemId}/total-quantity")
    public ResponseEntity<Integer> getTotalQuantitySoldForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(saleLineItemService.getTotalQuantitySoldForItem(itemId));
    }
    
    @Operation(summary = "Create sale line item")
    @PostMapping
    public ResponseEntity<?> createSaleLineItem(@RequestBody SaleLineItem saleLineItem) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(saleLineItemService.create(saleLineItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update sale line item")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSaleLineItem(@PathVariable Long id, @RequestBody SaleLineItem saleLineItem) {
        try {
            return ResponseEntity.ok(saleLineItemService.update(id, saleLineItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete sale line item")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSaleLineItem(@PathVariable Long id) {
        try {
            saleLineItemService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count sale line items")
    @GetMapping("/count")
    public ResponseEntity<Long> countSaleLineItems() {
        return ResponseEntity.ok(saleLineItemService.count());
    }
}
