package com.pos.backend.mapper;

import com.pos.backend.domain.TaxRate;
import com.pos.backend.dto.TaxRateDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between TaxRate entity and TaxRateDTO.
 */
@Component
public class TaxRateMapper {

    public TaxRate toEntity(TaxRateDTO.Request request) {
        TaxRate taxRate = new TaxRate();
        taxRate.setRate(request.getRate());
        taxRate.setEffectiveDate(request.getEffectiveDate());
        // taxCategory is set by the service layer
        taxRate.setDescription(request.getDescription());
        return taxRate;
    }

    public TaxRateDTO.Response toResponse(TaxRate taxRate) {
        return TaxRateDTO.Response.builder()
                .id(taxRate.getId())
                .rate(taxRate.getRate())
                .effectiveDate(taxRate.getEffectiveDate())
                .taxCategoryId(taxRate.getTaxCategory() != null ? taxRate.getTaxCategory().getId() : null)
                .taxCategoryName(taxRate.getTaxCategory() != null ? taxRate.getTaxCategory().getCategory() : null)
                .description(taxRate.getDescription())
                .isActive(taxRate.getIsActive())
                .build();
    }

    public void updateEntity(TaxRate taxRate, TaxRateDTO.Request request) {
        taxRate.setRate(request.getRate());
        taxRate.setEffectiveDate(request.getEffectiveDate());
        // taxCategory is set by the service layer
        taxRate.setDescription(request.getDescription());
    }
}
