package com.pos.backend.dto;

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
        private String number;
        private Long storeId;
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
