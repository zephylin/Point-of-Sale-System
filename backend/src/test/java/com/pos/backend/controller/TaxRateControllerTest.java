package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.TaxRate;
import com.pos.backend.dto.TaxRateDTO;
import com.pos.backend.mapper.TaxRateMapper;
import com.pos.backend.service.TaxRateService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxRateController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TaxRateController Integration Tests")
class TaxRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaxRateService taxRateService;

    @MockBean
    private TaxRateMapper taxRateMapper;

    private TaxRate sampleTaxRate;
    private TaxRateDTO.Request sampleRequest;
    private TaxRateDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleTaxRate = new TaxRate();
        sampleTaxRate.setId(1L);
        sampleTaxRate.setRate(new BigDecimal("0.0875"));
        sampleTaxRate.setEffectiveDate(LocalDate.of(2024, 1, 1));
        sampleTaxRate.setIsActive(true);

        sampleRequest = TaxRateDTO.Request.builder()
                .rate(new BigDecimal("0.0875"))
                .effectiveDate(LocalDate.of(2024, 1, 1))
                .taxCategoryId(1L)
                .description("Standard rate")
                .build();

        sampleResponse = TaxRateDTO.Response.builder()
                .id(1L)
                .rate(new BigDecimal("0.0875"))
                .effectiveDate(LocalDate.of(2024, 1, 1))
                .taxCategoryId(1L)
                .taxCategoryName("Food")
                .description("Standard rate")
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("GET /api/tax-rates")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of tax rates")
        void shouldReturnTaxRates() throws Exception {
            when(taxRateService.findAll()).thenReturn(List.of(sampleTaxRate));
            when(taxRateMapper.toResponse(any(TaxRate.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-rates"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].rate", is(0.0875)));
        }
    }

    @Nested
    @DisplayName("GET /api/tax-rates/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnTaxRate() throws Exception {
            when(taxRateService.findById(1L)).thenReturn(Optional.of(sampleTaxRate));
            when(taxRateMapper.toResponse(sampleTaxRate)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-rates/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rate", is(0.0875)));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(taxRateService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/tax-rates/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/tax-rates/category/{taxCategoryId}")
    class GetByCategory {

        @Test
        @DisplayName("should return tax rates by category")
        void shouldReturnByCategory() throws Exception {
            when(taxRateService.findByTaxCategory(1L)).thenReturn(List.of(sampleTaxRate));
            when(taxRateMapper.toResponse(any(TaxRate.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-rates/category/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/tax-rates/category/{id}/current")
    class GetCurrentRate {

        @Test
        @DisplayName("should return current rate for category")
        void shouldReturnCurrent() throws Exception {
            when(taxRateService.getCurrentRateForCategory(1L)).thenReturn(Optional.of(sampleTaxRate));
            when(taxRateMapper.toResponse(sampleTaxRate)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-rates/category/1/current"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rate", is(0.0875)));
        }

        @Test
        @DisplayName("should return 404 when no current rate")
        void shouldReturn404() throws Exception {
            when(taxRateService.getCurrentRateForCategory(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/tax-rates/category/999/current"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/tax-rates")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(taxRateMapper.toEntity(any(TaxRateDTO.Request.class))).thenReturn(sampleTaxRate);
            when(taxRateService.createWithIds(any(TaxRate.class), eq(1L))).thenReturn(sampleTaxRate);
            when(taxRateMapper.toResponse(any(TaxRate.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/tax-rates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when rate is null")
        void shouldReturn400WhenRateNull() throws Exception {
            TaxRateDTO.Request bad = TaxRateDTO.Request.builder()
                    .effectiveDate(LocalDate.of(2024, 1, 1))
                    .taxCategoryId(1L)
                    .build();

            mockMvc.perform(post("/api/tax-rates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when rate exceeds max")
        void shouldReturn400WhenRateTooHigh() throws Exception {
            TaxRateDTO.Request bad = TaxRateDTO.Request.builder()
                    .rate(new BigDecimal("1.5000"))
                    .effectiveDate(LocalDate.of(2024, 1, 1))
                    .taxCategoryId(1L)
                    .build();

            mockMvc.perform(post("/api/tax-rates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/tax-rates/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(taxRateMapper.toEntity(any(TaxRateDTO.Request.class))).thenReturn(sampleTaxRate);
            when(taxRateService.updateWithIds(eq(1L), any(TaxRate.class), eq(1L))).thenReturn(sampleTaxRate);
            when(taxRateMapper.toResponse(any(TaxRate.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/tax-rates/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/tax-rates/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(taxRateService).delete(1L);

            mockMvc.perform(delete("/api/tax-rates/1"))
                    .andExpect(status().isNoContent());

            verify(taxRateService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/tax-rates/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(taxRateService.count()).thenReturn(8L);

            mockMvc.perform(get("/api/tax-rates/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("8"));
        }
    }
}
