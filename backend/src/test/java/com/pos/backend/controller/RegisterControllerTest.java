package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Register;
import com.pos.backend.dto.RegisterDTO;
import com.pos.backend.mapper.RegisterMapper;
import com.pos.backend.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.pos.backend.security.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RegisterController Integration Tests")
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private RegisterService registerService;

    @MockBean
    private RegisterMapper registerMapper;

    private Register sampleRegister;
    private RegisterDTO.Request sampleRequest;
    private RegisterDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleRegister = new Register();
        sampleRegister.setId(1L);
        sampleRegister.setNumber("R001");
        sampleRegister.setIsActive(true);
        sampleRegister.setStatus("ACTIVE");

        sampleRequest = RegisterDTO.Request.builder()
                .number("R001")
                .storeId(1L)
                .description("Main register")
                .build();

        sampleResponse = RegisterDTO.Response.builder()
                .id(1L)
                .number("R001")
                .storeId(1L)
                .storeName("Quick Mart")
                .description("Main register")
                .isActive(true)
                .status("ACTIVE")
                .build();
    }

    @Nested
    @DisplayName("GET /api/registers")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of registers")
        void shouldReturnRegisters() throws Exception {
            when(registerService.findAll()).thenReturn(List.of(sampleRegister));
            when(registerMapper.toResponse(any(Register.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/registers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].number", is("R001")));
        }
    }

    @Nested
    @DisplayName("GET /api/registers/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnRegister() throws Exception {
            when(registerService.findById(1L)).thenReturn(Optional.of(sampleRegister));
            when(registerMapper.toResponse(sampleRegister)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/registers/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("R001")));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(registerService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/registers/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/registers/number/{number}")
    class GetByNumber {

        @Test
        @DisplayName("should return register by number")
        void shouldReturnByNumber() throws Exception {
            when(registerService.findByNumber("R001")).thenReturn(Optional.of(sampleRegister));
            when(registerMapper.toResponse(sampleRegister)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/registers/number/R001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("R001")));
        }
    }

    @Nested
    @DisplayName("GET /api/registers/store/{storeId}")
    class GetByStore {

        @Test
        @DisplayName("should return registers by store")
        void shouldReturnByStore() throws Exception {
            when(registerService.findByStore(1L)).thenReturn(List.of(sampleRegister));
            when(registerMapper.toResponse(any(Register.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/registers/store/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/registers/active")
    class GetActive {

        @Test
        @DisplayName("should return active registers")
        void shouldReturnActive() throws Exception {
            when(registerService.findAllActive()).thenReturn(List.of(sampleRegister));
            when(registerMapper.toResponse(any(Register.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/registers/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isActive", is(true)));
        }
    }

    @Nested
    @DisplayName("POST /api/registers")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(registerMapper.toEntity(any(RegisterDTO.Request.class))).thenReturn(sampleRegister);
            when(registerService.createWithIds(any(Register.class), eq(1L))).thenReturn(sampleRegister);
            when(registerMapper.toResponse(any(Register.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/registers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number", is("R001")));
        }

        @Test
        @DisplayName("should return 400 when number is blank")
        void shouldReturn400WhenNumberBlank() throws Exception {
            RegisterDTO.Request bad = RegisterDTO.Request.builder()
                    .number("")
                    .storeId(1L)
                    .build();

            mockMvc.perform(post("/api/registers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when storeId is null")
        void shouldReturn400WhenStoreIdNull() throws Exception {
            RegisterDTO.Request bad = RegisterDTO.Request.builder()
                    .number("R001")
                    .build();

            mockMvc.perform(post("/api/registers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/registers/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(registerMapper.toEntity(any(RegisterDTO.Request.class))).thenReturn(sampleRegister);
            when(registerService.updateWithIds(eq(1L), any(Register.class), eq(1L))).thenReturn(sampleRegister);
            when(registerMapper.toResponse(any(Register.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/registers/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/registers/{id}/status")
    class UpdateStatus {

        @Test
        @DisplayName("should return 200 when status updated")
        void shouldUpdateStatus() throws Exception {
            when(registerService.updateStatus(eq(1L), eq("MAINTENANCE"))).thenReturn(sampleRegister);
            when(registerMapper.toResponse(sampleRegister)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/registers/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "MAINTENANCE"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/registers/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(registerService).delete(1L);

            mockMvc.perform(delete("/api/registers/1"))
                    .andExpect(status().isNoContent());

            verify(registerService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/registers/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(registerService.count()).thenReturn(3L);

            mockMvc.perform(get("/api/registers/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("3"));
        }
    }
}
