package com.pos.backend.mapper;

import com.pos.backend.domain.TaxCategory;
import com.pos.backend.dto.TaxCategoryDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between TaxCategory entity and TaxCategoryDTO.
 */
@Component
public class TaxCategoryMapper {

    public TaxCategory toEntity(TaxCategoryDTO.Request request) {
        TaxCategory taxCategory = new TaxCategory();
        taxCategory.setCategory(request.getCategory());
        taxCategory.setDescription(request.getDescription());
        return taxCategory;
    }

    public TaxCategoryDTO.Response toResponse(TaxCategory taxCategory) {
        return TaxCategoryDTO.Response.builder()
                .id(taxCategory.getId())
                .category(taxCategory.getCategory())
                .description(taxCategory.getDescription())
                .isActive(taxCategory.getIsActive())
                .build();
    }

    public void updateEntity(TaxCategory taxCategory, TaxCategoryDTO.Request request) {
        taxCategory.setCategory(request.getCategory());
        taxCategory.setDescription(request.getDescription());
    }
}
