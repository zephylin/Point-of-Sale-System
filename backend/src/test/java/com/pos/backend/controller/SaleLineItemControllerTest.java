package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.dto.SaleLineItemDTO;
import com.pos.backend.mapper.SaleLineItemMapper;
import com.pos.backend.service.SaleLineItemService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleLineItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SaleLineItemController Integration Tests")
class SaleLineItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private SaleLineItemService saleLineItemService;

    @MockBean
    private SaleLineItemMapper saleLineItemMapper;

    private SaleLineItem sampleLineItem;
    private SaleLineItemDTO.Request sampleRequest;
    private SaleLineItemDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleLineItem = new SaleLineItem();
        sampleLineItem.setId(1L);
        sampleLineItem.setQuantity(2);
        sampleLineItem.setUnitPrice(new BigDecimal("5.99"));

        sampleRequest = SaleLineItemDTO.Request.builder()
                .saleId(1L)
                .itemId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("5.99"))
                .build();

        sampleResponse = SaleLineItemDTO.Response.builder()
                .id(1L)
                .saleId(1L)
                .itemId(1L)
                .itemDescription("Milk")
                .quantity(2)
                .unitPrice(new BigDecimal("5.99"))
                .extendedPrice(new BigDecimal("11.98"))
                .build();
    }

    @Nested
    @DisplayName("GET /api/sale-line-items")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of line items")
        void shouldReturnLineItems() throws Exception {
            when(saleLineItemService.findAll()).thenReturn(List.of(sampleLineItem));
            when(saleLineItemMapper.toResponse(any(SaleLineItem.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sale-line-items"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].quantity", is(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/sale-line-items/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnLineItem() throws Exception {
            when(saleLineItemService.findById(1L)).thenReturn(Optional.of(sampleLineItem));
            when(saleLineItemMapper.toResponse(sampleLineItem)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sale-line-items/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.itemDescription", is("Milk")));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(saleLineItemService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/sale-line-items/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/sale-line-items/sale/{saleId}")
    class GetBySale {

        @Test
        @DisplayName("should return line items by sale")
        void shouldReturnBySale() throws Exception {
            when(saleLineItemService.findBySale(1L)).thenReturn(List.of(sampleLineItem));
            when(saleLineItemMapper.toResponse(any(SaleLineItem.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/sale-line-items/sale/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/sale-line-items/item/{itemId}/total-quantity")
    class GetTotalQuantity {

        @Test
        @DisplayName("should return total quantity sold")
        void shouldReturnTotalQuantity() throws Exception {
            when(saleLineItemService.getTotalQuantitySoldForItem(1L)).thenReturn(50);

            mockMvc.perform(get("/api/sale-line-items/item/1/total-quantity"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("50"));
        }
    }

    @Nested
    @DisplayName("POST /api/sale-line-items")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(saleLineItemMapper.toEntity(any(SaleLineItemDTO.Request.class))).thenReturn(sampleLineItem);
            when(saleLineItemService.createWithIds(any(SaleLineItem.class), eq(1L), eq(1L))).thenReturn(sampleLineItem);
            when(saleLineItemMapper.toResponse(any(SaleLineItem.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/sale-line-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when quantity is zero")
        void shouldReturn400WhenQuantityZero() throws Exception {
            SaleLineItemDTO.Request bad = SaleLineItemDTO.Request.builder()
                    .saleId(1L)
                    .itemId(1L)
                    .quantity(0)
                    .unitPrice(new BigDecimal("5.99"))
                    .build();

            mockMvc.perform(post("/api/sale-line-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when saleId is null")
        void shouldReturn400WhenSaleIdNull() throws Exception {
            SaleLineItemDTO.Request bad = SaleLineItemDTO.Request.builder()
                    .itemId(1L)
                    .quantity(2)
                    .unitPrice(new BigDecimal("5.99"))
                    .build();

            mockMvc.perform(post("/api/sale-line-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/sale-line-items/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(saleLineItemMapper.toEntity(any(SaleLineItemDTO.Request.class))).thenReturn(sampleLineItem);
            when(saleLineItemService.updateWithIds(eq(1L), any(SaleLineItem.class), eq(1L), eq(1L))).thenReturn(sampleLineItem);
            when(saleLineItemMapper.toResponse(any(SaleLineItem.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/sale-line-items/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sale-line-items/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(saleLineItemService).delete(1L);

            mockMvc.perform(delete("/api/sale-line-items/1"))
                    .andExpect(status().isNoContent());

            verify(saleLineItemService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/sale-line-items/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(saleLineItemService.count()).thenReturn(15L);

            mockMvc.perform(get("/api/sale-line-items/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("15"));
        }
    }
}
