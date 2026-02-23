package com.pos.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Sale entity.
 */
public class SaleDTO {

    /**
     * Request DTO - Used for creating a new sale.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Session ID is required")
        private Long sessionId;

        @NotNull(message = "Store ID is required")
        private Long storeId;

        @NotNull(message = "Cashier ID is required")
        private Long cashierId;

        private Boolean taxFree;

        @Size(max = 1000, message = "Notes must be at most 1000 characters")
        private String notes;
    }

    /**
     * Response DTO - Returned by the API with computed totals.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long sessionId;
        private Long storeId;
        private Long cashierId;
        private LocalDateTime dateTime;
        private BigDecimal subtotal;
        private BigDecimal tax;
        private BigDecimal total;
        private BigDecimal amountPaid;
        private BigDecimal change;
        private Boolean taxFree;
        private String status;
        private String paymentMethod;
        private String notes;
    }
}
