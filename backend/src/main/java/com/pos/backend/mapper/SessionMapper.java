package com.pos.backend.mapper;

import com.pos.backend.domain.Session;
import com.pos.backend.dto.SessionDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Session entity and SessionDTO.
 */
@Component
public class SessionMapper {

    public Session toEntity(SessionDTO.Request request) {
        Session session = new Session();
        // cashier and register are set by the service layer
        session.setStartingCash(request.getStartingCash());
        session.setNotes(request.getNotes());
        return session;
    }

    public SessionDTO.Response toResponse(Session session) {
        return SessionDTO.Response.builder()
                .id(session.getId())
                .cashierId(session.getCashier() != null ? session.getCashier().getId() : null)
                .cashierName(session.getCashier() != null && session.getCashier().getPerson() != null
                        ? session.getCashier().getPerson().getFullName()
                        : (session.getCashier() != null ? session.getCashier().getNumber() : null))
                .registerId(session.getRegister() != null ? session.getRegister().getId() : null)
                .registerNumber(session.getRegister() != null ? session.getRegister().getNumber() : null)
                .startDateTime(session.getStartDateTime())
                .endDateTime(session.getEndDateTime())
                .startingCash(session.getStartingCash())
                .endingCash(session.getEndingCash())
                .expectedCash(session.getExpectedCash())
                .cashVariance(session.getCashVariance())
                .status(session.getStatus())
                .notes(session.getNotes())
                .build();
    }

    public void updateEntity(Session session, SessionDTO.Request request) {
        // cashier and register are set by the service layer
        session.setStartingCash(request.getStartingCash());
        session.setNotes(request.getNotes());
    }
}
