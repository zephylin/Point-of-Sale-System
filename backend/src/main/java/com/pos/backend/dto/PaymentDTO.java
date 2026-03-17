package com.pos.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Payment entity.
 */
public class PaymentDTO {

    /**
     * Request DTO - Used for creating/updating a payment.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Sale ID is required")
        private Long saleId;

        @NotNull(message = "Payment type is required")
        @Pattern(regexp = "CASH|CREDIT|CHECK", message = "Payment type must be CASH, CREDIT, or CHECK")
        private String paymentType;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        private BigDecimal amountTendered;

        // Credit-specific
        @Size(max = 50, message = "Card type must be at most 50 characters")
        private String cardType;

        @Size(min = 4, max = 4, message = "Card last four must be exactly 4 digits")
        private String cardLastFour;

        private LocalDate expirationDate;

        // Check-specific
        @Size(max = 20, message = "Routing number must be at most 20 characters")
        private String routingNumber;

        @Size(max = 20, message = "Account number must be at most 20 characters")
        private String accountNumber;

        @Size(max = 50, message = "Check number must be at most 50 characters")
        private String checkNumber;
    }

    /**
     * Response DTO - Returned by the API.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long saleId;
        private String paymentType;
        private BigDecimal amount;
        private BigDecimal amountTendered;
        private BigDecimal changeDue;
        private Boolean countAsCash;
        private LocalDateTime paymentDateTime;

        // Authorized payment fields
        private String authorizationCode;
        private Boolean authorized;

        // Credit-specific
        private String cardType;
        private String cardLastFour;
        private LocalDate expirationDate;

        // Check-specific
        private String routingNumber;
        private String accountNumber;
        private String checkNumber;
    }
}
