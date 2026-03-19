package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Session;
import com.pos.backend.dto.SessionDTO;
import com.pos.backend.mapper.SessionMapper;
import com.pos.backend.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SessionController Integration Tests")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session sampleSession;
    private SessionDTO.Request sampleRequest;
    private SessionDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleSession = new Session();
        sampleSession.setId(1L);
        sampleSession.setStartDateTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        sampleSession.setStartingCash(new BigDecimal("100.00"));
        sampleSession.setStatus("ACTIVE");

        sampleRequest = SessionDTO.Request.builder()
                .cashierId(1L)
                .registerId(1L)
                .startingCash(new BigDecimal("100.00"))
                .build();

        sampleResponse = SessionDTO.Response.builder()
                .id(1L)
                .cashierId(1L)
                .cashierName("David Martin")
                .registerId(1L)
                .registerNumber("R001")
                .startDateTime(LocalDateTime.of(2024, 1, 15, 9, 0))
                .startingCash(new BigDecimal("100.00"))
                .status("ACTIVE")
                .build();
    }

    @Nested
    @DisplayName("GET /api/sessions")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of sessions")
        void shouldReturnSessions() throws Exception {
            when(sessionService.findAll()).thenReturn(List.of(sampleSession));
            when(sessionMapper.toResponse(any(Session.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is("ACTIVE")));
        }
    }

    @Nested
    @DisplayName("GET /api/sessions/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnSession() throws Exception {
            when(sessionService.findById(1L)).thenReturn(Optional.of(sampleSession));
            when(sessionMapper.toResponse(sampleSession)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sessions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(sessionService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/sessions/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/sessions/cashier/{cashierId}")
    class GetByCashier {

        @Test
        @DisplayName("should return sessions by cashier")
        void shouldReturnByCashier() throws Exception {
            when(sessionService.findByCashier(1L)).thenReturn(List.of(sampleSession));
            when(sessionMapper.toResponse(any(Session.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sessions/cashier/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/sessions/cashier/{cashierId}/active")
    class GetActiveByCashier {

        @Test
        @DisplayName("should return active session for cashier")
        void shouldReturnActive() throws Exception {
            when(sessionService.findActiveSessionForCashier(1L)).thenReturn(Optional.of(sampleSession));
            when(sessionMapper.toResponse(sampleSession)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sessions/cashier/1/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("should return 404 when no active session")
        void shouldReturn404() throws Exception {
            when(sessionService.findActiveSessionForCashier(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/sessions/cashier/999/active"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/sessions")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(sessionMapper.toEntity(any(SessionDTO.Request.class))).thenReturn(sampleSession);
            when(sessionService.createWithIds(any(Session.class), eq(1L), eq(1L))).thenReturn(sampleSession);
            when(sessionMapper.toResponse(any(Session.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/sessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("should return 400 when cashierId is null")
        void shouldReturn400WhenCashierIdNull() throws Exception {
            SessionDTO.Request bad = SessionDTO.Request.builder()
                    .registerId(1L)
                    .startingCash(new BigDecimal("100.00"))
                    .build();

            mockMvc.perform(post("/api/sessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when registerId is null")
        void shouldReturn400WhenRegisterIdNull() throws Exception {
            SessionDTO.Request bad = SessionDTO.Request.builder()
                    .cashierId(1L)
                    .startingCash(new BigDecimal("100.00"))
                    .build();

            mockMvc.perform(post("/api/sessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/sessions/{id}/close")
    class Close {

        @Test
        @DisplayName("should return 200 when session closed")
        void shouldClose() throws Exception {
            when(sessionService.closeSession(eq(1L), any(BigDecimal.class))).thenReturn(sampleSession);
            when(sessionMapper.toResponse(sampleSession)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/sessions/1/close")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("endingCash", "150.00"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sessions/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(sessionService).delete(1L);

            mockMvc.perform(delete("/api/sessions/1"))
                    .andExpect(status().isNoContent());

            verify(sessionService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/sessions/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(sessionService.count()).thenReturn(10L);

            mockMvc.perform(get("/api/sessions/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("10"));
        }
    }
}
