package com.pos.backend.mapper;

import com.pos.backend.domain.Cashier;
import com.pos.backend.dto.CashierDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Cashier entity and CashierDTO.
 * Note: Password is accepted in Request but NEVER returned in Response.
 */
@Component
public class CashierMapper {

    public Cashier toEntity(CashierDTO.Request request) {
        Cashier cashier = new Cashier();
        cashier.setNumber(request.getNumber());
        cashier.setPassword(request.getPassword());
        cashier.setPersonId(request.getPersonId());
        cashier.setStoreId(request.getStoreId());
        cashier.setRole(request.getRole() != null ? request.getRole() : "Cashier");
        return cashier;
    }

    public CashierDTO.Response toResponse(Cashier cashier) {
        return CashierDTO.Response.builder()
                .id(cashier.getId())
                .number(cashier.getNumber())
                .personId(cashier.getPersonId())
                .storeId(cashier.getStoreId())
                .isActive(cashier.getIsActive())
                .hireDate(cashier.getHireDate())
                .terminationDate(cashier.getTerminationDate())
                .role(cashier.getRole())
                .build();
        // NOTE: password is intentionally excluded!
    }

    public void updateEntity(Cashier cashier, CashierDTO.Request request) {
        cashier.setNumber(request.getNumber());
        if (request.getPassword() != null) {
            cashier.setPassword(request.getPassword());
        }
        cashier.setPersonId(request.getPersonId());
        cashier.setStoreId(request.getStoreId());
        if (request.getRole() != null) {
            cashier.setRole(request.getRole());
        }
    }
}
