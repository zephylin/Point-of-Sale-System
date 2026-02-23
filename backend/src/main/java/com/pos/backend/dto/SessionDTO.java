package com.pos.backend.dto;

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
        private Long cashierId;
        private Long registerId;
        private BigDecimal startingCash;
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
        private Long registerId;
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
