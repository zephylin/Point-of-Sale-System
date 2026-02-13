package com.pos.backend.controller;

import com.pos.backend.domain.Cashier;
import com.pos.backend.service.CashierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashiers")
@RequiredArgsConstructor
@Tag(name = "Cashiers", description = "Cashier management APIs")
public class CashierController {
    
    private final CashierService cashierService;
    
    @Operation(summary = "Get all cashiers")
    @GetMapping
    public ResponseEntity<List<Cashier>> getAllCashiers() {
        return ResponseEntity.ok(cashierService.findAll());
    }
    
    @Operation(summary = "Get cashier by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cashier> getCashierById(@PathVariable Long id) {
        return cashierService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get cashier by number")
    @GetMapping("/number/{number}")
    public ResponseEntity<Cashier> getCashierByNumber(@PathVariable String number) {
        return cashierService.findByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get cashiers by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Cashier>> getCashiersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(cashierService.findByStore(storeId));
    }
    
    @Operation(summary = "Get active cashiers")
    @GetMapping("/active")
    public ResponseEntity<List<Cashier>> getActiveCashiers() {
        return ResponseEntity.ok(cashierService.findAllActive());
    }
    
    @Operation(summary = "Get cashiers by role")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Cashier>> getCashiersByRole(@PathVariable String role) {
        return ResponseEntity.ok(cashierService.findByRole(role));
    }
    
    @Operation(summary = "Create cashier")
    @PostMapping
    public ResponseEntity<?> createCashier(@RequestBody Cashier cashier) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(cashierService.create(cashier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update cashier")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCashier(@PathVariable Long id, @RequestBody Cashier cashier) {
        try {
            return ResponseEntity.ok(cashierService.update(id, cashier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Terminate cashier")
    @PatchMapping("/{id}/terminate")
    public ResponseEntity<?> terminateCashier(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cashierService.terminate(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete cashier")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCashier(@PathVariable Long id) {
        try {
            cashierService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count cashiers")
    @GetMapping("/count")
    public ResponseEntity<Long> countCashiers() {
        return ResponseEntity.ok(cashierService.count());
    }
    
    @Operation(summary = "Count cashiers by store")
    @GetMapping("/count/store/{storeId}")
    public ResponseEntity<Long> countCashiersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(cashierService.countByStore(storeId));
    }
}
