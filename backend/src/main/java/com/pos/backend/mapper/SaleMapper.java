package com.pos.backend.mapper;

import com.pos.backend.domain.Sale;
import com.pos.backend.dto.SaleDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Sale entity and SaleDTO.
 */
@Component
public class SaleMapper {

    public Sale toEntity(SaleDTO.Request request) {
        Sale sale = new Sale();
        sale.setSessionId(request.getSessionId());
        sale.setStoreId(request.getStoreId());
        sale.setCashierId(request.getCashierId());
        sale.setTaxFree(request.getTaxFree() != null ? request.getTaxFree() : false);
        sale.setNotes(request.getNotes());
        return sale;
    }

    public SaleDTO.Response toResponse(Sale sale) {
        return SaleDTO.Response.builder()
                .id(sale.getId())
                .sessionId(sale.getSessionId())
                .storeId(sale.getStoreId())
                .cashierId(sale.getCashierId())
                .dateTime(sale.getDateTime())
                .subtotal(sale.getSubtotal())
                .tax(sale.getTax())
                .total(sale.getTotal())
                .amountPaid(sale.getAmountPaid())
                .change(sale.getChange())
                .taxFree(sale.getTaxFree())
                .status(sale.getStatus())
                .paymentMethod(sale.getPaymentMethod())
                .notes(sale.getNotes())
                .build();
    }

    public void updateEntity(Sale sale, SaleDTO.Request request) {
        sale.setSessionId(request.getSessionId());
        sale.setStoreId(request.getStoreId());
        sale.setCashierId(request.getCashierId());
        if (request.getTaxFree() != null) {
            sale.setTaxFree(request.getTaxFree());
        }
        sale.setNotes(request.getNotes());
    }
}
