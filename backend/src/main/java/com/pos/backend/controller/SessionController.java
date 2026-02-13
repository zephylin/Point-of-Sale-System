package com.pos.backend.controller;

import com.pos.backend.domain.Session;
import com.pos.backend.service.SessionService;
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
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management APIs")
public class SessionController {
    
    private final SessionService sessionService;
    
    @Operation(summary = "Get all sessions")
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        return ResponseEntity.ok(sessionService.findAll());
    }
    
    @Operation(summary = "Get session by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        return sessionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sessions by cashier")
    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<Session>> getSessionsByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(sessionService.findByCashier(cashierId));
    }
    
    @Operation(summary = "Get sessions by register")
    @GetMapping("/register/{registerId}")
    public ResponseEntity<List<Session>> getSessionsByRegister(@PathVariable Long registerId) {
        return ResponseEntity.ok(sessionService.findByRegister(registerId));
    }
    
    @Operation(summary = "Get sessions by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Session>> getSessionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(sessionService.findByStatus(status));
    }
    
    @Operation(summary = "Get active session for cashier")
    @GetMapping("/cashier/{cashierId}/active")
    public ResponseEntity<Session> getActiveSessionForCashier(@PathVariable Long cashierId) {
        return sessionService.findActiveSessionForCashier(cashierId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get active session for register")
    @GetMapping("/register/{registerId}/active")
    public ResponseEntity<Session> getActiveSessionForRegister(@PathVariable Long registerId) {
        return sessionService.findActiveSessionForRegister(registerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sessions by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<Session>> getSessionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return ResponseEntity.ok(sessionService.findByDateRange(start, end));
    }
    
    @Operation(summary = "Create session")
    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody Session session) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.create(session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update session")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody Session session) {
        try {
            return ResponseEntity.ok(sessionService.update(id, session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Close session")
    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeSession(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            BigDecimal endingCash = new BigDecimal(request.get("endingCash"));
            return ResponseEntity.ok(sessionService.closeSession(id, endingCash));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Delete session")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) {
        try {
            sessionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Count sessions")
    @GetMapping("/count")
    public ResponseEntity<Long> countSessions() {
        return ResponseEntity.ok(sessionService.count());
    }
    
    @Operation(summary = "Count sessions by cashier")
    @GetMapping("/count/cashier/{cashierId}")
    public ResponseEntity<Long> countSessionsByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(sessionService.countByCashier(cashierId));
    }
}
