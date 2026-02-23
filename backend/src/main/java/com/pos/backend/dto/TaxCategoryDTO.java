package com.pos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for TaxCategory entity.
 */
public class TaxCategoryDTO {

    /**
     * Request DTO - Used for creating/updating a tax category.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must be at most 100 characters")
        private String category;

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
        private String category;
        private String description;
        private Boolean isActive;
    }
}
