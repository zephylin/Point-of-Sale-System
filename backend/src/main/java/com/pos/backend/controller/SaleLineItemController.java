package com.pos.backend.controller;

import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.dto.SaleLineItemDTO;
import com.pos.backend.mapper.SaleLineItemMapper;
import com.pos.backend.service.SaleLineItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sale-line-items")
@RequiredArgsConstructor
@Tag(name = "Sale Line Items", description = "Sale line item management APIs")
public class SaleLineItemController {
    
    private final SaleLineItemService saleLineItemService;
    private final SaleLineItemMapper saleLineItemMapper;
    
    @Operation(summary = "Get all sale line items")
    @GetMapping
    public ResponseEntity<List<SaleLineItemDTO.Response>> getAllSaleLineItems() {
        List<SaleLineItemDTO.Response> items = saleLineItemService.findAll().stream()
                .map(saleLineItemMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get sale line item by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SaleLineItemDTO.Response> getSaleLineItemById(@PathVariable Long id) {
        return saleLineItemService.findById(id)
                .map(saleLineItemMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sale line items by sale")
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleLineItemDTO.Response>> getSaleLineItemsBySale(@PathVariable Long saleId) {
        List<SaleLineItemDTO.Response> items = saleLineItemService.findBySale(saleId).stream()
                .map(saleLineItemMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get sale line items by item")
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<SaleLineItemDTO.Response>> getSaleLineItemsByItem(@PathVariable Long itemId) {
        List<SaleLineItemDTO.Response> items = saleLineItemService.findByItem(itemId).stream()
                .map(saleLineItemMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Get total quantity sold for item")
    @GetMapping("/item/{itemId}/total-quantity")
    public ResponseEntity<Integer> getTotalQuantitySoldForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(saleLineItemService.getTotalQuantitySoldForItem(itemId));
    }
    
    @Operation(summary = "Create sale line item")
    @PostMapping
    public ResponseEntity<?> createSaleLineItem(@Valid @RequestBody SaleLineItemDTO.Request request) {
        SaleLineItem lineItem = saleLineItemMapper.toEntity(request);
        SaleLineItem created = saleLineItemService.createWithIds(lineItem, request.getSaleId(), request.getItemId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saleLineItemMapper.toResponse(created));
    }
    
    @Operation(summary = "Update sale line item")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSaleLineItem(@PathVariable Long id, @Valid @RequestBody SaleLineItemDTO.Request request) {
        SaleLineItem lineItem = saleLineItemMapper.toEntity(request);
        SaleLineItem updated = saleLineItemService.updateWithIds(id, lineItem, request.getSaleId(), request.getItemId());
        return ResponseEntity.ok(saleLineItemMapper.toResponse(updated));
    }
    
    @Operation(summary = "Delete sale line item")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSaleLineItem(@PathVariable Long id) {
        saleLineItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Count sale line items")
    @GetMapping("/count")
    public ResponseEntity<Long> countSaleLineItems() {
        return ResponseEntity.ok(saleLineItemService.count());
    }
}
