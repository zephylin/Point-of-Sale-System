package com.pos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @NotBlank(message = "Cashier number is required")
        @Size(max = 50, message = "Cashier number must be at most 50 characters")
        private String number;

        @NotBlank(message = "Password is required")
        @Size(min = 4, max = 255, message = "Password must be between 4 and 255 characters")
        private String password;

        @NotNull(message = "Person ID is required")
        private Long personId;

        @NotNull(message = "Store ID is required")
        private Long storeId;

        @Size(max = 50, message = "Role must be at most 50 characters")
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
