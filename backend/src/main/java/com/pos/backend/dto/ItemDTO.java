package com.pos.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Item entity.
 */
public class ItemDTO {

    /**
     * Request DTO - Used for creating/updating an item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Item number is required")
        @Size(max = 50, message = "Item number must be at most 50 characters")
        private String number;

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must be at most 500 characters")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be at least 0.00")
        private BigDecimal price;

        @DecimalMin(value = "0.00", message = "Cost must be at least 0.00")
        private BigDecimal cost;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be at least 0")
        private Integer quantity;

        @Min(value = 0, message = "Minimum quantity must be at least 0")
        private Integer minQuantity;

        @Min(value = 0, message = "Maximum quantity must be at least 0")
        private Integer maxQuantity;

        private Long taxCategoryId;
        private Long storeId;

        @Size(max = 50, message = "Barcode must be at most 50 characters")
        private String barcode;

        @Size(max = 50, message = "SKU must be at most 50 characters")
        private String sku;

        @Size(max = 100, message = "Brand must be at most 100 characters")
        private String brand;

        @Size(max = 100, message = "Category must be at most 100 characters")
        private String category;

        private Boolean isTaxable;
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
        private String description;
        private BigDecimal price;
        private BigDecimal cost;
        private Integer quantity;
        private Integer minQuantity;
        private Integer maxQuantity;
        private Long taxCategoryId;
        private String taxCategoryName;
        private Long storeId;
        private String storeName;
        private String barcode;
        private String sku;
        private String brand;
        private String category;
        private Boolean isActive;
        private Boolean isTaxable;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
    }
}
