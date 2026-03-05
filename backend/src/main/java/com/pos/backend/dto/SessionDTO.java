package com.pos.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Session entity.
 */
public class SessionDTO {

    /**
     * Request DTO - Used for starting a new session.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Cashier ID is required")
        private Long cashierId;

        @NotNull(message = "Register ID is required")
        private Long registerId;

        @DecimalMin(value = "0.00", message = "Starting cash must be at least 0.00")
        private BigDecimal startingCash;

        @Size(max = 1000, message = "Notes must be at most 1000 characters")
        private String notes;
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
        private Long cashierId;
        private String cashierName;
        private Long registerId;
        private String registerNumber;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private BigDecimal startingCash;
        private BigDecimal endingCash;
        private BigDecimal expectedCash;
        private BigDecimal cashVariance;
        private String status;
        private String notes;
    }
}
