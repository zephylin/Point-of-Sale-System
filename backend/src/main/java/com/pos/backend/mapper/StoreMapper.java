package com.pos.backend.mapper;

import com.pos.backend.domain.Store;
import com.pos.backend.dto.StoreDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Store entity and StoreDTO.
 */
@Component
public class StoreMapper {

    public Store toEntity(StoreDTO.Request request) {
        Store store = new Store();
        store.setNumber(request.getNumber());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setCity(request.getCity());
        store.setState(request.getState());
        store.setZip(request.getZip());
        store.setPhone(request.getPhone());
        store.setEmail(request.getEmail());
        store.setManager(request.getManager());
        return store;
    }

    public StoreDTO.Response toResponse(Store store) {
        return StoreDTO.Response.builder()
                .id(store.getId())
                .number(store.getNumber())
                .name(store.getName())
                .address(store.getAddress())
                .city(store.getCity())
                .state(store.getState())
                .zip(store.getZip())
                .phone(store.getPhone())
                .email(store.getEmail())
                .manager(store.getManager())
                .isActive(store.getIsActive())
                .openedDate(store.getOpenedDate())
                .closedDate(store.getClosedDate())
                .build();
    }

    public void updateEntity(Store store, StoreDTO.Request request) {
        store.setNumber(request.getNumber());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setCity(request.getCity());
        store.setState(request.getState());
        store.setZip(request.getZip());
        store.setPhone(request.getPhone());
        store.setEmail(request.getEmail());
        store.setManager(request.getManager());
    }
}
