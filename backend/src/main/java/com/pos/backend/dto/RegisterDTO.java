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
 * Data Transfer Object for Register entity.
 */
public class RegisterDTO {

    /**
     * Request DTO - Used for creating/updating a register.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Register number is required")
        @Size(max = 50, message = "Register number must be at most 50 characters")
        private String number;

        @NotNull(message = "Store ID is required")
        private Long storeId;

        @Size(max = 200, message = "Description must be at most 200 characters")
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
        private String number;
        private Long storeId;
        private String description;
        private Boolean isActive;
        private String status;
        private LocalDateTime installedDate;
        private LocalDateTime lastMaintenanceDate;
    }
}
