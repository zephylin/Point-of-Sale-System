package com.pos.backend.dto;

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
        private BigDecimal rate;
        private LocalDate effectiveDate;
        private Long taxCategoryId;
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
        private String description;
        private Boolean isActive;
    }
}
