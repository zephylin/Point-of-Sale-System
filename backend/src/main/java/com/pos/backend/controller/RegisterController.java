package com.pos.backend.controller;

import com.pos.backend.domain.Register;
import com.pos.backend.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registers")
@RequiredArgsConstructor
@Tag(name = "Registers", description = "Register management APIs")
public class RegisterController {
    
    private final RegisterService registerService;
    
    @Operation(summary = "Get all registers")
    @GetMapping
    public ResponseEntity<List<Register>> getAllRegisters() {
        return ResponseEntity.ok(registerService.findAll());
    }
    
    @Operation(summary = "Get register by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Register> getRegisterById(@PathVariable Long id) {
        return registerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get register by number")
    @GetMapping("/number/{number}")
    public ResponseEntity<Register> getRegisterByNumber(@PathVariable String number) {
        return registerService.findByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get registers by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Register>> getRegistersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(registerService.findByStore(storeId));
    }
    
    @Operation(summary = "Get active registers")
    @GetMapping("/active")
    public ResponseEntity<List<Register>> getActiveRegisters() {
        return ResponseEntity.ok(registerService.findAllActive());
    }
    
    @Operation(summary = "Get registers by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Register>> getRegistersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(registerService.findByStatus(status));
    }
    
    @Operation(summary = "Create register")
    @PostMapping
    public ResponseEntity<?> createRegister(@RequestBody Register register) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(registerService.create(register));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update register")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegister(@PathVariable Long id, @RequestBody Register register) {
        try {
            return ResponseEntity.ok(registerService.update(id, register));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update register status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateRegisterStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().body("Status is required");
            }
            return ResponseEntity.ok(registerService.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete register")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegister(@PathVariable Long id) {
        try {
            registerService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count registers")
    @GetMapping("/count")
    public ResponseEntity<Long> countRegisters() {
        return ResponseEntity.ok(registerService.count());
    }
    
    @Operation(summary = "Count registers by store")
    @GetMapping("/count/store/{storeId}")
    public ResponseEntity<Long> countRegistersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(registerService.countByStore(storeId));
    }
}
