package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Register;
import com.pos.backend.domain.Session;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.RegisterRepository;
import com.pos.backend.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final CashierRepository cashierRepository;
    private final RegisterRepository registerRepository;
    
    @Transactional(readOnly = true)
    public List<Session> findAll() {
        return sessionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Session> findByCashier(Long cashierId) {
        return sessionRepository.findByCashier_Id(cashierId);
    }
    
    @Transactional(readOnly = true)
    public List<Session> findByRegister(Long registerId) {
        return sessionRepository.findByRegister_Id(registerId);
    }
    
    @Transactional(readOnly = true)
    public List<Session> findByStatus(String status) {
        return sessionRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public Optional<Session> findActiveSessionForCashier(Long cashierId) {
        return sessionRepository.findTopByCashier_IdAndStatusOrderByStartDateTimeDesc(cashierId, "ACTIVE");
    }
    
    @Transactional(readOnly = true)
    public Optional<Session> findActiveSessionForRegister(Long registerId) {
        return sessionRepository.findTopByRegister_IdAndStatusOrderByStartDateTimeDesc(registerId, "ACTIVE");
    }
    
    @Transactional(readOnly = true)
    public List<Session> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return sessionRepository.findSessionsByDateRange(startDate, endDate);
    }
    
    public Session create(Session session) {
        validateSession(session);
        if (session.getStartDateTime() == null) {
            session.setStartDateTime(LocalDateTime.now());
        }
        if (session.getStatus() == null) {
            session.setStatus("ACTIVE");
        }
        return sessionRepository.save(session);
    }

    /**
     * Create a session, resolving cashier and register from IDs
     */
    public Session createWithIds(Session session, Long cashierId, Long registerId) {
        resolveRelationships(session, cashierId, registerId);
        return create(session);
    }
    
    public Session update(Long id, Session session) {
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
        validateSession(session);
        existing.setCashier(session.getCashier());
        existing.setRegister(session.getRegister());
        existing.setStartingCash(session.getStartingCash());
        existing.setEndingCash(session.getEndingCash());
        existing.setExpectedCash(session.getExpectedCash());
        existing.setCashVariance(session.getCashVariance());
        existing.setStatus(session.getStatus());
        existing.setNotes(session.getNotes());
        return sessionRepository.save(existing);
    }

    /**
     * Update a session, resolving cashier and register from IDs
     */
    public Session updateWithIds(Long id, Session session, Long cashierId, Long registerId) {
        resolveRelationships(session, cashierId, registerId);
        return update(id, session);
    }
    
    public Session closeSession(Long id, BigDecimal endingCash) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
        
        if (!"ACTIVE".equals(session.getStatus())) {
            throw new IllegalArgumentException("Only active sessions can be closed");
        }
        
        session.setEndDateTime(LocalDateTime.now());
        session.setEndingCash(endingCash);
        session.setStatus("CLOSED");
        
        // Calculate variance if we have expected cash
        if (session.getExpectedCash() != null && endingCash != null) {
            BigDecimal variance = endingCash.subtract(session.getExpectedCash());
            session.setCashVariance(variance);
        }
        
        return sessionRepository.save(session);
    }
    
    public void delete(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return sessionRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countByCashier(Long cashierId) {
        return sessionRepository.countByCashier_Id(cashierId);
    }

    private void resolveRelationships(Session session, Long cashierId, Long registerId) {
        if (cashierId != null) {
            Cashier cashier = cashierRepository.findById(cashierId)
                    .orElseThrow(() -> new IllegalArgumentException("Cashier not found with id: " + cashierId));
            session.setCashier(cashier);
        }
        if (registerId != null) {
            Register register = registerRepository.findById(registerId)
                    .orElseThrow(() -> new IllegalArgumentException("Register not found with id: " + registerId));
            session.setRegister(register);
        }
    }
    
    private void validateSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getCashier() == null) {
            throw new IllegalArgumentException("Cashier is required");
        }
        if (session.getRegister() == null) {
            throw new IllegalArgumentException("Register is required");
        }
    }
}
