package com.pos.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for SaleLineItem entity.
 */
public class SaleLineItemDTO {

    /**
     * Request DTO - Used for adding a line item to a sale.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Sale ID is required")
        private Long saleId;

        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.00", message = "Unit price must be at least 0.00")
        private BigDecimal unitPrice;

        @DecimalMin(value = "0.00", message = "Discount must be at least 0.00")
        private BigDecimal discount;

        @Size(max = 500, message = "Notes must be at most 500 characters")
        private String notes;
    }

    /**
     * Response DTO - Returned by the API with computed prices.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long saleId;
        private Long itemId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal extendedPrice;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
        private BigDecimal totalPrice;
        private BigDecimal discount;
        private String notes;
    }
}
