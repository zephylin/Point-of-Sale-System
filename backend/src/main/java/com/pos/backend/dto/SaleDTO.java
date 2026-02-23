package com.pos.backend.dto;

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
        private Long sessionId;
        private Long storeId;
        private Long cashierId;
        private Boolean taxFree;
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
