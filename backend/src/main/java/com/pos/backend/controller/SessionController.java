package com.pos.backend.controller;

import com.pos.backend.domain.Session;
import com.pos.backend.dto.SessionDTO;
import com.pos.backend.mapper.SessionMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management APIs")
public class SessionController {
    
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;
    
    @Operation(summary = "Get all sessions")
    @GetMapping
    public ResponseEntity<List<SessionDTO.Response>> getAllSessions() {
        List<SessionDTO.Response> sessions = sessionService.findAll().stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }
    
    @Operation(summary = "Get session by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO.Response> getSessionById(@PathVariable Long id) {
        return sessionService.findById(id)
                .map(sessionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sessions by cashier")
    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<SessionDTO.Response>> getSessionsByCashier(@PathVariable Long cashierId) {
        List<SessionDTO.Response> sessions = sessionService.findByCashier(cashierId).stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }
    
    @Operation(summary = "Get sessions by register")
    @GetMapping("/register/{registerId}")
    public ResponseEntity<List<SessionDTO.Response>> getSessionsByRegister(@PathVariable Long registerId) {
        List<SessionDTO.Response> sessions = sessionService.findByRegister(registerId).stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }
    
    @Operation(summary = "Get sessions by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SessionDTO.Response>> getSessionsByStatus(@PathVariable String status) {
        List<SessionDTO.Response> sessions = sessionService.findByStatus(status).stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }
    
    @Operation(summary = "Get active session for cashier")
    @GetMapping("/cashier/{cashierId}/active")
    public ResponseEntity<SessionDTO.Response> getActiveSessionForCashier(@PathVariable Long cashierId) {
        return sessionService.findActiveSessionForCashier(cashierId)
                .map(sessionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get active session for register")
    @GetMapping("/register/{registerId}/active")
    public ResponseEntity<SessionDTO.Response> getActiveSessionForRegister(@PathVariable Long registerId) {
        return sessionService.findActiveSessionForRegister(registerId)
                .map(sessionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get sessions by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<SessionDTO.Response>> getSessionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<SessionDTO.Response> sessions = sessionService.findByDateRange(start, end).stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }
    
    @Operation(summary = "Create session")
    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody SessionDTO.Request request) {
        try {
            Session session = sessionMapper.toEntity(request);
            Session created = sessionService.create(session);
            return ResponseEntity.status(HttpStatus.CREATED).body(sessionMapper.toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Update session")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody SessionDTO.Request request) {
        try {
            Session session = sessionMapper.toEntity(request);
            Session updated = sessionService.update(id, session);
            return ResponseEntity.ok(sessionMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Close session")
    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeSession(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            BigDecimal endingCash = new BigDecimal(request.get("endingCash"));
            Session closed = sessionService.closeSession(id, endingCash);
            return ResponseEntity.ok(sessionMapper.toResponse(closed));
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
