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
        session.setCashierId(request.getCashierId());
        session.setRegisterId(request.getRegisterId());
        session.setStartingCash(request.getStartingCash());
        session.setNotes(request.getNotes());
        return session;
    }

    public SessionDTO.Response toResponse(Session session) {
        return SessionDTO.Response.builder()
                .id(session.getId())
                .cashierId(session.getCashierId())
                .registerId(session.getRegisterId())
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
        session.setCashierId(request.getCashierId());
        session.setRegisterId(request.getRegisterId());
        session.setStartingCash(request.getStartingCash());
        session.setNotes(request.getNotes());
    }
}
