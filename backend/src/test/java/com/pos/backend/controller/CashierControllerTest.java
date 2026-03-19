package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Cashier;
import com.pos.backend.dto.CashierDTO;
import com.pos.backend.mapper.CashierMapper;
import com.pos.backend.service.CashierService;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashierController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CashierController Integration Tests")
class CashierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CashierService cashierService;

    @MockBean
    private CashierMapper cashierMapper;

    private Cashier sampleCashier;
    private CashierDTO.Request sampleRequest;
    private CashierDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleCashier = new Cashier();
        sampleCashier.setId(1L);
        sampleCashier.setNumber("C001");
        sampleCashier.setIsActive(true);
        sampleCashier.setRole("CASHIER");

        sampleRequest = CashierDTO.Request.builder()
                .number("C001")
                .password("pass123")
                .personId(1L)
                .storeId(1L)
                .role("CASHIER")
                .build();

        sampleResponse = CashierDTO.Response.builder()
                .id(1L)
                .number("C001")
                .personId(1L)
                .personName("David Martin")
                .storeId(1L)
                .storeName("Quick Mart")
                .isActive(true)
                .role("CASHIER")
                .build();
    }

    @Nested
    @DisplayName("GET /api/cashiers")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of cashiers")
        void shouldReturnCashiers() throws Exception {
            when(cashierService.findAll()).thenReturn(List.of(sampleCashier));
            when(cashierMapper.toResponse(any(Cashier.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/cashiers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].number", is("C001")));
        }
    }

    @Nested
    @DisplayName("GET /api/cashiers/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when cashier found")
        void shouldReturnCashier() throws Exception {
            when(cashierService.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(cashierMapper.toResponse(sampleCashier)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/cashiers/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("C001")));
        }

        @Test
        @DisplayName("should return 404 when cashier not found")
        void shouldReturn404() throws Exception {
            when(cashierService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/cashiers/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/cashiers/number/{number}")
    class GetByNumber {

        @Test
        @DisplayName("should return cashier by number")
        void shouldReturnByNumber() throws Exception {
            when(cashierService.findByNumber("C001")).thenReturn(Optional.of(sampleCashier));
            when(cashierMapper.toResponse(sampleCashier)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/cashiers/number/C001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("C001")));
        }

        @Test
        @DisplayName("should return 404 when number not found")
        void shouldReturn404() throws Exception {
            when(cashierService.findByNumber("X999")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/cashiers/number/X999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/cashiers/store/{storeId}")
    class GetByStore {

        @Test
        @DisplayName("should return cashiers by store")
        void shouldReturnByStore() throws Exception {
            when(cashierService.findByStore(1L)).thenReturn(List.of(sampleCashier));
            when(cashierMapper.toResponse(any(Cashier.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/cashiers/store/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/cashiers/active")
    class GetActive {

        @Test
        @DisplayName("should return active cashiers")
        void shouldReturnActive() throws Exception {
            when(cashierService.findAllActive()).thenReturn(List.of(sampleCashier));
            when(cashierMapper.toResponse(any(Cashier.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/cashiers/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isActive", is(true)));
        }
    }

    @Nested
    @DisplayName("POST /api/cashiers")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(cashierMapper.toEntity(any(CashierDTO.Request.class))).thenReturn(sampleCashier);
            when(cashierService.createWithIds(any(Cashier.class), eq(1L), eq(1L))).thenReturn(sampleCashier);
            when(cashierMapper.toResponse(any(Cashier.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/cashiers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number", is("C001")));
        }

        @Test
        @DisplayName("should return 400 when number is blank")
        void shouldReturn400WhenNumberBlank() throws Exception {
            CashierDTO.Request bad = CashierDTO.Request.builder()
                    .number("")
                    .password("pass123")
                    .personId(1L)
                    .storeId(1L)
                    .build();

            mockMvc.perform(post("/api/cashiers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when password too short")
        void shouldReturn400WhenPasswordTooShort() throws Exception {
            CashierDTO.Request bad = CashierDTO.Request.builder()
                    .number("C001")
                    .password("ab")
                    .personId(1L)
                    .storeId(1L)
                    .build();

            mockMvc.perform(post("/api/cashiers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when personId is null")
        void shouldReturn400WhenPersonIdNull() throws Exception {
            CashierDTO.Request bad = CashierDTO.Request.builder()
                    .number("C001")
                    .password("pass123")
                    .storeId(1L)
                    .build();

            mockMvc.perform(post("/api/cashiers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/cashiers/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(cashierMapper.toEntity(any(CashierDTO.Request.class))).thenReturn(sampleCashier);
            when(cashierService.updateWithIds(eq(1L), any(Cashier.class), eq(1L), eq(1L))).thenReturn(sampleCashier);
            when(cashierMapper.toResponse(any(Cashier.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/cashiers/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/cashiers/{id}/terminate")
    class Terminate {

        @Test
        @DisplayName("should return 200 when terminated")
        void shouldTerminate() throws Exception {
            when(cashierService.terminate(1L)).thenReturn(sampleCashier);
            when(cashierMapper.toResponse(sampleCashier)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/cashiers/1/terminate"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/cashiers/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(cashierService).delete(1L);

            mockMvc.perform(delete("/api/cashiers/1"))
                    .andExpect(status().isNoContent());

            verify(cashierService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/cashiers/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(cashierService.count()).thenReturn(5L);

            mockMvc.perform(get("/api/cashiers/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }
    }
}
