package com.pos.backend.controller;

import com.pos.backend.domain.Register;
import com.pos.backend.dto.RegisterDTO;
import com.pos.backend.mapper.RegisterMapper;
import com.pos.backend.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registers")
@RequiredArgsConstructor
@Tag(name = "Registers", description = "Register management APIs")
public class RegisterController {
    
    private final RegisterService registerService;
    private final RegisterMapper registerMapper;
    
    @Operation(summary = "Get all registers")
    @GetMapping
    public ResponseEntity<List<RegisterDTO.Response>> getAllRegisters() {
        List<RegisterDTO.Response> registers = registerService.findAll().stream()
                .map(registerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registers);
    }
    
    @Operation(summary = "Get register by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RegisterDTO.Response> getRegisterById(@PathVariable Long id) {
        return registerService.findById(id)
                .map(registerMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get register by number")
    @GetMapping("/number/{number}")
    public ResponseEntity<RegisterDTO.Response> getRegisterByNumber(@PathVariable String number) {
        return registerService.findByNumber(number)
                .map(registerMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get registers by store")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<RegisterDTO.Response>> getRegistersByStore(@PathVariable Long storeId) {
        List<RegisterDTO.Response> registers = registerService.findByStore(storeId).stream()
                .map(registerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registers);
    }
    
    @Operation(summary = "Get active registers")
    @GetMapping("/active")
    public ResponseEntity<List<RegisterDTO.Response>> getActiveRegisters() {
        List<RegisterDTO.Response> registers = registerService.findAllActive().stream()
                .map(registerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registers);
    }
    
    @Operation(summary = "Get registers by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RegisterDTO.Response>> getRegistersByStatus(@PathVariable String status) {
        List<RegisterDTO.Response> registers = registerService.findByStatus(status).stream()
                .map(registerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registers);
    }
    
    @Operation(summary = "Create register")
    @PostMapping
    public ResponseEntity<?> createRegister(@Valid @RequestBody RegisterDTO.Request request) {
        Register register = registerMapper.toEntity(request);
        Register created = registerService.createWithIds(register, request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(registerMapper.toResponse(created));
    }
    
    @Operation(summary = "Update register")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegister(@PathVariable Long id, @Valid @RequestBody RegisterDTO.Request request) {
        Register register = registerMapper.toEntity(request);
        Register updated = registerService.updateWithIds(id, register, request.getStoreId());
        return ResponseEntity.ok(registerMapper.toResponse(updated));
    }
    
    @Operation(summary = "Update register status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateRegisterStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        Register updated = registerService.updateStatus(id, status);
        return ResponseEntity.ok(registerMapper.toResponse(updated));
    }
    
    @Operation(summary = "Delete register")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegister(@PathVariable Long id) {
        registerService.delete(id);
        return ResponseEntity.noContent().build();
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
