package com.pos.backend.mapper;

import com.pos.backend.domain.Item;
import com.pos.backend.dto.ItemDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Item entity and ItemDTO.
 */
@Component
public class ItemMapper {

    public Item toEntity(ItemDTO.Request request) {
        Item item = new Item();
        item.setNumber(request.getNumber());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCost(request.getCost());
        item.setQuantity(request.getQuantity());
        item.setMinQuantity(request.getMinQuantity());
        item.setMaxQuantity(request.getMaxQuantity());
        // taxCategory and store are set by the service layer
        item.setBarcode(request.getBarcode());
        item.setSku(request.getSku());
        item.setBrand(request.getBrand());
        item.setCategory(request.getCategory());
        item.setIsTaxable(request.getIsTaxable() != null ? request.getIsTaxable() : true);
        return item;
    }

    public ItemDTO.Response toResponse(Item item) {
        return ItemDTO.Response.builder()
                .id(item.getId())
                .number(item.getNumber())
                .description(item.getDescription())
                .price(item.getPrice())
                .cost(item.getCost())
                .quantity(item.getQuantity())
                .minQuantity(item.getMinQuantity())
                .maxQuantity(item.getMaxQuantity())
                .taxCategoryId(item.getTaxCategory() != null ? item.getTaxCategory().getId() : null)
                .taxCategoryName(item.getTaxCategory() != null ? item.getTaxCategory().getCategory() : null)
                .storeId(item.getStore() != null ? item.getStore().getId() : null)
                .storeName(item.getStore() != null ? item.getStore().getName() : null)
                .barcode(item.getBarcode())
                .sku(item.getSku())
                .brand(item.getBrand())
                .category(item.getCategory())
                .isActive(item.getIsActive())
                .isTaxable(item.getIsTaxable())
                .createdDate(item.getCreatedDate())
                .modifiedDate(item.getModifiedDate())
                .build();
    }

    public void updateEntity(Item item, ItemDTO.Request request) {
        item.setNumber(request.getNumber());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCost(request.getCost());
        item.setQuantity(request.getQuantity());
        item.setMinQuantity(request.getMinQuantity());
        item.setMaxQuantity(request.getMaxQuantity());
        // taxCategory and store are set by the service layer
        item.setBarcode(request.getBarcode());
        item.setSku(request.getSku());
        item.setBrand(request.getBrand());
        item.setCategory(request.getCategory());
        if (request.getIsTaxable() != null) {
            item.setIsTaxable(request.getIsTaxable());
        }
    }
}
