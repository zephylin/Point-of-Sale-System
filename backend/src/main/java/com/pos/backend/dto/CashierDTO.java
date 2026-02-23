package com.pos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Cashier entity.
 */
public class CashierDTO {

    /**
     * Request DTO - Used for creating/updating a cashier.
     * Includes password for creation but it will NOT be returned in responses.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String number;
        private String password;
        private Long personId;
        private Long storeId;
        private String role;
    }

    /**
     * Response DTO - Returned by the API.
     * Does NOT include password for security.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String number;
        private Long personId;
        private Long storeId;
        private Boolean isActive;
        private LocalDateTime hireDate;
        private LocalDateTime terminationDate;
        private String role;
    }
}
