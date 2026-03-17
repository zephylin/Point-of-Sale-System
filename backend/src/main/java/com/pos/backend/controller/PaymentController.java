package com.pos.backend.controller;

import com.pos.backend.domain.Payment;
import com.pos.backend.dto.PaymentDTO;
import com.pos.backend.mapper.PaymentMapper;
import com.pos.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Operation(summary = "Get all payments")
    @GetMapping
    public ResponseEntity<List<PaymentDTO.Response>> getAllPayments() {
        List<PaymentDTO.Response> payments = paymentService.findAll().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO.Response> getPaymentById(@PathVariable Long id) {
        return paymentService.findById(id)
                .map(paymentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get payments for a sale")
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentDTO.Response>> getPaymentsBySale(@PathVariable Long saleId) {
        List<PaymentDTO.Response> payments = paymentService.findBySale(saleId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get payments by type")
    @GetMapping("/type/{paymentType}")
    public ResponseEntity<List<PaymentDTO.Response>> getPaymentsByType(@PathVariable String paymentType) {
        List<PaymentDTO.Response> payments = paymentService.findByPaymentType(paymentType.toUpperCase()).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get payments by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO.Response>> getPaymentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<PaymentDTO.Response> payments = paymentService.findByDateRange(start, end).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get total payments for a sale")
    @GetMapping("/total/sale/{saleId}")
    public ResponseEntity<BigDecimal> getTotalPaymentsBySale(@PathVariable Long saleId) {
        return ResponseEntity.ok(paymentService.getTotalPaymentsBySale(saleId));
    }

    @Operation(summary = "Get total cash for a session")
    @GetMapping("/total/cash/session/{sessionId}")
    public ResponseEntity<BigDecimal> getTotalCashBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(paymentService.getTotalCashBySession(sessionId));
    }

    @Operation(summary = "Create payment")
    @PostMapping
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO.Request request) {
        Payment payment = paymentMapper.toEntity(request);
        Payment created = paymentService.createWithSaleId(payment, request.getSaleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponse(created));
    }

    @Operation(summary = "Update payment")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id,
                                           @Valid @RequestBody PaymentDTO.Request request) {
        Payment payment = paymentMapper.toEntity(request);
        // Resolve sale for update
        Payment existing = paymentService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        payment.setSale(existing.getSale());
        Payment updated = paymentService.update(id, payment);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @Operation(summary = "Delete payment")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Count payments")
    @GetMapping("/count")
    public ResponseEntity<Long> countPayments() {
        return ResponseEntity.ok(paymentService.count());
    }

    @Operation(summary = "Count payments for a sale")
    @GetMapping("/count/sale/{saleId}")
    public ResponseEntity<Long> countPaymentsBySale(@PathVariable Long saleId) {
        return ResponseEntity.ok(paymentService.countBySale(saleId));
    }
}
