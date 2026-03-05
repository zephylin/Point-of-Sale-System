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
        // session, store, cashier are set by the service layer
        sale.setTaxFree(request.getTaxFree() != null ? request.getTaxFree() : false);
        sale.setNotes(request.getNotes());
        return sale;
    }

    public SaleDTO.Response toResponse(Sale sale) {
        return SaleDTO.Response.builder()
                .id(sale.getId())
                .sessionId(sale.getSession() != null ? sale.getSession().getId() : null)
                .storeId(sale.getStore() != null ? sale.getStore().getId() : null)
                .storeName(sale.getStore() != null ? sale.getStore().getName() : null)
                .cashierId(sale.getCashier() != null ? sale.getCashier().getId() : null)
                .cashierName(sale.getCashier() != null && sale.getCashier().getPerson() != null
                        ? sale.getCashier().getPerson().getFullName()
                        : (sale.getCashier() != null ? sale.getCashier().getNumber() : null))
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
        // session, store, cashier are set by the service layer
        if (request.getTaxFree() != null) {
            sale.setTaxFree(request.getTaxFree());
        }
        sale.setNotes(request.getNotes());
    }
}
