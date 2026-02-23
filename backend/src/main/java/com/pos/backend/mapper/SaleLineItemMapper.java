package com.pos.backend.mapper;

import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.dto.SaleLineItemDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between SaleLineItem entity and SaleLineItemDTO.
 */
@Component
public class SaleLineItemMapper {

    public SaleLineItem toEntity(SaleLineItemDTO.Request request) {
        SaleLineItem lineItem = new SaleLineItem();
        lineItem.setSaleId(request.getSaleId());
        lineItem.setItemId(request.getItemId());
        lineItem.setQuantity(request.getQuantity());
        lineItem.setUnitPrice(request.getUnitPrice());
        lineItem.setDiscount(request.getDiscount());
        lineItem.setNotes(request.getNotes());
        return lineItem;
    }

    public SaleLineItemDTO.Response toResponse(SaleLineItem lineItem) {
        return SaleLineItemDTO.Response.builder()
                .id(lineItem.getId())
                .saleId(lineItem.getSaleId())
                .itemId(lineItem.getItemId())
                .quantity(lineItem.getQuantity())
                .unitPrice(lineItem.getUnitPrice())
                .extendedPrice(lineItem.getExtendedPrice())
                .taxRate(lineItem.getTaxRate())
                .taxAmount(lineItem.getTaxAmount())
                .totalPrice(lineItem.getTotalPrice())
                .discount(lineItem.getDiscount())
                .notes(lineItem.getNotes())
                .build();
    }

    public void updateEntity(SaleLineItem lineItem, SaleLineItemDTO.Request request) {
        lineItem.setSaleId(request.getSaleId());
        lineItem.setItemId(request.getItemId());
        lineItem.setQuantity(request.getQuantity());
        lineItem.setUnitPrice(request.getUnitPrice());
        lineItem.setDiscount(request.getDiscount());
        lineItem.setNotes(request.getNotes());
    }
}
