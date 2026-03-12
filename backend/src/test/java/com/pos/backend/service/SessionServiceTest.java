package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Register;
import com.pos.backend.domain.Session;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.RegisterRepository;
import com.pos.backend.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService Unit Tests")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private CashierRepository cashierRepository;

    @Mock
    private RegisterRepository registerRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session sampleSession;
    private Cashier sampleCashier;
    private Register sampleRegister;

    @BeforeEach
    void setUp() {
        sampleCashier = new Cashier();
        sampleCashier.setId(1L);
        sampleRegister = new Register();
        sampleRegister.setId(1L);

        sampleSession = new Session();
        sampleSession.setId(1L);
        sampleSession.setCashier(sampleCashier);
        sampleSession.setRegister(sampleRegister);
        sampleSession.setStartDateTime(LocalDateTime.now());
        sampleSession.setStartingCash(new BigDecimal("200.00"));
        sampleSession.setExpectedCash(new BigDecimal("350.00"));
        sampleSession.setStatus("ACTIVE");
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all sessions")
        void shouldReturnAllSessions() {
            when(sessionRepository.findAll()).thenReturn(List.of(sampleSession));

            List<Session> result = sessionService.findAll();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return session when found")
        void shouldReturnSessionWhenFound() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));

            Optional<Session> result = sessionService.findById(1L);

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find sessions by cashier")
        void shouldFindByCashier() {
            when(sessionRepository.findByCashier_Id(1L)).thenReturn(List.of(sampleSession));

            List<Session> result = sessionService.findByCashier(1L);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find sessions by register")
        void shouldFindByRegister() {
            when(sessionRepository.findByRegister_Id(1L)).thenReturn(List.of(sampleSession));

            List<Session> result = sessionService.findByRegister(1L);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find sessions by status")
        void shouldFindByStatus() {
            when(sessionRepository.findByStatus("ACTIVE")).thenReturn(List.of(sampleSession));

            List<Session> result = sessionService.findByStatus("ACTIVE");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find active session for cashier")
        void shouldFindActiveSessionForCashier() {
            when(sessionRepository.findTopByCashier_IdAndStatusOrderByStartDateTimeDesc(1L, "ACTIVE"))
                    .thenReturn(Optional.of(sampleSession));

            Optional<Session> result = sessionService.findActiveSessionForCashier(1L);

            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("should find active session for register")
        void shouldFindActiveSessionForRegister() {
            when(sessionRepository.findTopByRegister_IdAndStatusOrderByStartDateTimeDesc(1L, "ACTIVE"))
                    .thenReturn(Optional.of(sampleSession));

            Optional<Session> result = sessionService.findActiveSessionForRegister(1L);

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create session successfully")
        void shouldCreateSessionSuccessfully() {
            when(sessionRepository.save(any(Session.class))).thenReturn(sampleSession);

            Session result = sessionService.create(sampleSession);

            assertThat(result).isNotNull();
            verify(sessionRepository).save(any(Session.class));
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleSession.setStartDateTime(null);
            sampleSession.setStatus(null);

            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.create(sampleSession);

            assertThat(result.getStartDateTime()).isNotNull();
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("should throw when session is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> sessionService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session cannot be null");
        }

        @Test
        @DisplayName("should throw when cashier is missing")
        void shouldThrowWhenCashierMissing() {
            sampleSession.setCashier(null);

            assertThatThrownBy(() -> sessionService.create(sampleSession))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier is required");
        }

        @Test
        @DisplayName("should throw when register is missing")
        void shouldThrowWhenRegisterMissing() {
            sampleSession.setRegister(null);

            assertThatThrownBy(() -> sessionService.create(sampleSession))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Register is required");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve relationships from IDs")
        void shouldResolveRelationships() {
            Session newSession = new Session();
            newSession.setCashier(sampleCashier);
            newSession.setRegister(sampleRegister);

            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(registerRepository.findById(1L)).thenReturn(Optional.of(sampleRegister));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.createWithIds(newSession, 1L, 1L);

            assertThat(result.getCashier()).isEqualTo(sampleCashier);
            assertThat(result.getRegister()).isEqualTo(sampleRegister);
        }

        @Test
        @DisplayName("should throw when cashier not found")
        void shouldThrowWhenCashierNotFound() {
            Session newSession = new Session();

            when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.createWithIds(newSession, 99L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier not found with id: 99");
        }

        @Test
        @DisplayName("should throw when register not found")
        void shouldThrowWhenRegisterNotFound() {
            Session newSession = new Session();
            newSession.setCashier(sampleCashier);

            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(registerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.createWithIds(newSession, 1L, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Register not found with id: 99");
        }
    }

    @Nested
    @DisplayName("closeSession")
    class CloseSession {

        @Test
        @DisplayName("should close active session with variance calculation")
        void shouldCloseActiveSession() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.closeSession(1L, new BigDecimal("340.00"));

            assertThat(result.getStatus()).isEqualTo("CLOSED");
            assertThat(result.getEndDateTime()).isNotNull();
            assertThat(result.getEndingCash()).isEqualByComparingTo(new BigDecimal("340.00"));
            // variance = endingCash(340) - expectedCash(350) = -10
            assertThat(result.getCashVariance()).isEqualByComparingTo(new BigDecimal("-10.00"));
        }

        @Test
        @DisplayName("should throw when session not found")
        void shouldThrowWhenNotFound() {
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.closeSession(99L, BigDecimal.TEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session not found with id: 99");
        }

        @Test
        @DisplayName("should throw when session is not active")
        void shouldThrowWhenNotActive() {
            sampleSession.setStatus("CLOSED");
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));

            assertThatThrownBy(() -> sessionService.closeSession(1L, BigDecimal.TEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Only active sessions can be closed");
        }

        @Test
        @DisplayName("should handle null expected cash gracefully")
        void shouldHandleNullExpectedCash() {
            sampleSession.setExpectedCash(null);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.closeSession(1L, new BigDecimal("340.00"));

            assertThat(result.getStatus()).isEqualTo("CLOSED");
            assertThat(result.getCashVariance()).isNull();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update session successfully")
        void shouldUpdateSuccessfully() {
            Session updated = new Session();
            updated.setCashier(sampleCashier);
            updated.setRegister(sampleRegister);
            updated.setStartingCash(new BigDecimal("300.00"));
            updated.setStatus("ACTIVE");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.update(1L, updated);

            assertThat(result.getStartingCash()).isEqualByComparingTo(new BigDecimal("300.00"));
        }

        @Test
        @DisplayName("should throw when session not found for update")
        void shouldThrowWhenNotFound() {
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.update(99L, sampleSession))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session not found with id: 99");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete session")
        void shouldDeleteSession() {
            when(sessionRepository.existsById(1L)).thenReturn(true);

            sessionService.delete(1L);

            verify(sessionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when session not found")
        void shouldThrowWhenNotFound() {
            when(sessionRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> sessionService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session not found with id: 99");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return total count")
        void shouldReturnTotalCount() {
            when(sessionRepository.count()).thenReturn(5L);

            assertThat(sessionService.count()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should return count by cashier")
        void shouldReturnCountByCashier() {
            when(sessionRepository.countByCashier_Id(1L)).thenReturn(3L);

            assertThat(sessionService.countByCashier(1L)).isEqualTo(3L);
        }
    }
}
