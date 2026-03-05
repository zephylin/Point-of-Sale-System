package com.pos.backend.controller;

import com.pos.backend.domain.Cashier;
import com.pos.backend.dto.CashierDTO;
import com.pos.backend.mapper.CashierMapper;
import com.pos.backend.service.CashierService;
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
@RequestMapping("/api/cashiers")
@RequiredArgsConstructor
@Tag(name = "Cashiers", description = "Cashier management APIs")
public class CashierController {
    
    private final CashierService cashierService;
    private final CashierMapper cashierMapper;
    
    @Operation(summary = "Get all cashiers")
    @GetMapping
    public ResponseEntity<List<CashierDTO.Response>> getAllCashiers() {
        List<CashierDTO.Response> cashiers = cashierService.findAll().stream()
                .map(cashierMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cashiers);
    }
    
    @Operation(summary = "Get cashier by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CashierDTO.Response> getCashierById(@PathVariable Long id) {
        return cashierService.findById(id)
                .map(cashierMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get cashier by number")
    @GetMapping("/number/{number}")
    public ResponseEntity<CashierDTO.Response> getCashierByNumber(@PathVariable String number) {
        return cashierService.findByNumber(number)
                .map(cashierMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get cashiers by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CashierDTO.Response>> getCashiersByStore(@PathVariable Long storeId) {
        List<CashierDTO.Response> cashiers = cashierService.findByStore(storeId).stream()
                .map(cashierMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cashiers);
    }
    
    @Operation(summary = "Get active cashiers")
    @GetMapping("/active")
    public ResponseEntity<List<CashierDTO.Response>> getActiveCashiers() {
        List<CashierDTO.Response> cashiers = cashierService.findAllActive().stream()
                .map(cashierMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cashiers);
    }
    
    @Operation(summary = "Get cashiers by role")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<CashierDTO.Response>> getCashiersByRole(@PathVariable String role) {
        List<CashierDTO.Response> cashiers = cashierService.findByRole(role).stream()
                .map(cashierMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cashiers);
    }
    
    @Operation(summary = "Create cashier")
    @PostMapping
    public ResponseEntity<?> createCashier(@Valid @RequestBody CashierDTO.Request request) {
        Cashier cashier = cashierMapper.toEntity(request);
        Cashier created = cashierService.createWithIds(cashier, request.getPersonId(), request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cashierMapper.toResponse(created));
    }
    
    @Operation(summary = "Update cashier")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCashier(@PathVariable Long id, @Valid @RequestBody CashierDTO.Request request) {
        Cashier cashier = cashierMapper.toEntity(request);
        Cashier updated = cashierService.updateWithIds(id, cashier, request.getPersonId(), request.getStoreId());
        return ResponseEntity.ok(cashierMapper.toResponse(updated));
    }
    
    @Operation(summary = "Terminate cashier")
    @PatchMapping("/{id}/terminate")
    public ResponseEntity<?> terminateCashier(@PathVariable Long id) {
        Cashier terminated = cashierService.terminate(id);
        return ResponseEntity.ok(cashierMapper.toResponse(terminated));
    }
    
    @Operation(summary = "Delete cashier")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCashier(@PathVariable Long id) {
        cashierService.delete(id);
        return ResponseEntity.noContent().build();
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
