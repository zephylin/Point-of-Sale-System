package com.pos.backend.dto;

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
        private String number;
        private String description;
        private BigDecimal price;
        private BigDecimal cost;
        private Integer quantity;
        private Integer minQuantity;
        private Integer maxQuantity;
        private Long taxCategoryId;
        private Long storeId;
        private String barcode;
        private String sku;
        private String brand;
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
        private Long storeId;
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
