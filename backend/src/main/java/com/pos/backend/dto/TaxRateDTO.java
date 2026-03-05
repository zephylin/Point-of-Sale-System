package com.pos.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for TaxRate entity.
 */
public class TaxRateDTO {

    /**
     * Request DTO - Used for creating/updating a tax rate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Tax rate is required")
        @DecimalMin(value = "0.0000", message = "Tax rate must be at least 0.0000")
        @DecimalMax(value = "1.0000", message = "Tax rate must be at most 1.0000")
        private BigDecimal rate;

        @NotNull(message = "Effective date is required")
        private LocalDate effectiveDate;

        @NotNull(message = "Tax category ID is required")
        private Long taxCategoryId;

        @Size(max = 500, message = "Description must be at most 500 characters")
        private String description;
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
        private BigDecimal rate;
        private LocalDate effectiveDate;
        private Long taxCategoryId;
        private String taxCategoryName;
        private String description;
        private Boolean isActive;
    }
}
