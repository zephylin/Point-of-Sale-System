package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Sale;
import com.pos.backend.dto.SaleDTO;
import com.pos.backend.mapper.SaleMapper;
import com.pos.backend.service.SaleService;
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

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SaleController Integration Tests")
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SaleService saleService;

    @MockBean
    private SaleMapper saleMapper;

    private Sale sampleSale;
    private SaleDTO.Request sampleRequest;
    private SaleDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleSale = new Sale();
        sampleSale.setId(1L);
        sampleSale.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        sampleSale.setSubtotal(new BigDecimal("25.00"));
        sampleSale.setTax(new BigDecimal("2.19"));
        sampleSale.setTotal(new BigDecimal("27.19"));
        sampleSale.setStatus("COMPLETED");

        sampleRequest = SaleDTO.Request.builder()
                .sessionId(1L)
                .storeId(1L)
                .cashierId(1L)
                .build();

        sampleResponse = SaleDTO.Response.builder()
                .id(1L)
                .sessionId(1L)
                .storeId(1L)
                .storeName("Quick Mart")
                .cashierId(1L)
                .cashierName("David Martin")
                .dateTime(LocalDateTime.of(2024, 1, 15, 10, 30))
                .subtotal(new BigDecimal("25.00"))
                .tax(new BigDecimal("2.19"))
                .total(new BigDecimal("27.19"))
                .status("COMPLETED")
                .build();
    }

    @Nested
    @DisplayName("GET /api/sales")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of sales")
        void shouldReturnSales() throws Exception {
            when(saleService.findAll()).thenReturn(List.of(sampleSale));
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is("COMPLETED")));
        }
    }

    @Nested
    @DisplayName("GET /api/sales/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnSale() throws Exception {
            when(saleService.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(saleMapper.toResponse(sampleSale)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sales/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total", is(27.19)));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(saleService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/sales/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/sales/session/{sessionId}")
    class GetBySession {

        @Test
        @DisplayName("should return sales by session")
        void shouldReturnBySession() throws Exception {
            when(saleService.findBySession(1L)).thenReturn(List.of(sampleSale));
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sales/session/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/sales/total/store/{storeId}")
    class GetTotalByStore {

        @Test
        @DisplayName("should return total sales by store")
        void shouldReturnTotal() throws Exception {
            when(saleService.getTotalSalesByStore(1L)).thenReturn(new BigDecimal("500.00"));

            mockMvc.perform(get("/api/sales/total/store/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is(500.00)));
        }
    }

    @Nested
    @DisplayName("POST /api/sales")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(saleMapper.toEntity(any(SaleDTO.Request.class))).thenReturn(sampleSale);
            when(saleService.createWithIds(any(Sale.class), eq(1L), eq(1L), eq(1L))).thenReturn(sampleSale);
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when sessionId is null")
        void shouldReturn400WhenSessionIdNull() throws Exception {
            SaleDTO.Request bad = SaleDTO.Request.builder()
                    .storeId(1L)
                    .cashierId(1L)
                    .build();

            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/sales/{id}/complete")
    class Complete {

        @Test
        @DisplayName("should return 200 when completed")
        void shouldComplete() throws Exception {
            when(saleService.completeSale(eq(1L), any(BigDecimal.class), eq("CASH"))).thenReturn(sampleSale);
            when(saleMapper.toResponse(sampleSale)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/sales/1/complete")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("amountPaid", "30.00", "paymentMethod", "CASH"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/sales/{id}/void")
    class Void {

        @Test
        @DisplayName("should return 200 when voided")
        void shouldVoid() throws Exception {
            when(saleService.voidSale(eq(1L), eq("Customer request"))).thenReturn(sampleSale);
            when(saleMapper.toResponse(sampleSale)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/sales/1/void")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("reason", "Customer request"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sales/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(saleService).delete(1L);

            mockMvc.perform(delete("/api/sales/1"))
                    .andExpect(status().isNoContent());

            verify(saleService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/sales/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(saleService.count()).thenReturn(25L);

            mockMvc.perform(get("/api/sales/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("25"));
        }
    }
}
