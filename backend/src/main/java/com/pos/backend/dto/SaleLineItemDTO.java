package com.pos.backend.dto;

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
        private Long saleId;
        private Long itemId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
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
