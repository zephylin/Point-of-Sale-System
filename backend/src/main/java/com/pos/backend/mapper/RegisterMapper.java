package com.pos.backend.mapper;

import com.pos.backend.domain.Register;
import com.pos.backend.dto.RegisterDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Register entity and RegisterDTO.
 */
@Component
public class RegisterMapper {

    public Register toEntity(RegisterDTO.Request request) {
        Register register = new Register();
        register.setNumber(request.getNumber());
        register.setStoreId(request.getStoreId());
        register.setDescription(request.getDescription());
        return register;
    }

    public RegisterDTO.Response toResponse(Register register) {
        return RegisterDTO.Response.builder()
                .id(register.getId())
                .number(register.getNumber())
                .storeId(register.getStoreId())
                .description(register.getDescription())
                .isActive(register.getIsActive())
                .status(register.getStatus())
                .installedDate(register.getInstalledDate())
                .lastMaintenanceDate(register.getLastMaintenanceDate())
                .build();
    }

    public void updateEntity(Register register, RegisterDTO.Request request) {
        register.setNumber(request.getNumber());
        register.setStoreId(request.getStoreId());
        register.setDescription(request.getDescription());
    }
}
