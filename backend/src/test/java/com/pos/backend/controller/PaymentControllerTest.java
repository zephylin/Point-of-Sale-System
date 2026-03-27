package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Payment;
import com.pos.backend.dto.PaymentDTO;
import com.pos.backend.mapper.PaymentMapper;
import com.pos.backend.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PaymentController Integration Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentMapper paymentMapper;

    private Payment samplePayment;
    private PaymentDTO.Request sampleRequest;
    private PaymentDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        samplePayment = new Payment();
        samplePayment.setId(1L);
        samplePayment.setPaymentType("CASH");
        samplePayment.setAmount(new BigDecimal("30.00"));
        samplePayment.setAmountTendered(new BigDecimal("30.00"));

        sampleRequest = PaymentDTO.Request.builder()
                .saleId(1L)
                .paymentType("CASH")
                .amount(new BigDecimal("30.00"))
                .amountTendered(new BigDecimal("30.00"))
                .build();

        sampleResponse = PaymentDTO.Response.builder()
                .id(1L)
                .saleId(1L)
                .paymentType("CASH")
                .amount(new BigDecimal("30.00"))
                .amountTendered(new BigDecimal("30.00"))
                .changeDue(new BigDecimal("2.81"))
                .countAsCash(true)
                .build();
    }

    @Nested
    @DisplayName("GET /api/payments")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of payments")
        void shouldReturnPayments() throws Exception {
            when(paymentService.findAll()).thenReturn(List.of(samplePayment));
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/payments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].paymentType", is("CASH")));
        }
    }

    @Nested
    @DisplayName("GET /api/payments/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnPayment() throws Exception {
            when(paymentService.findById(1L)).thenReturn(Optional.of(samplePayment));
            when(paymentMapper.toResponse(samplePayment)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/payments/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentType", is("CASH")));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(paymentService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/payments/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/payments/sale/{saleId}")
    class GetBySale {

        @Test
        @DisplayName("should return payments by sale")
        void shouldReturnBySale() throws Exception {
            when(paymentService.findBySale(1L)).thenReturn(List.of(samplePayment));
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/payments/sale/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/payments/type/{paymentType}")
    class GetByType {

        @Test
        @DisplayName("should return payments by type")
        void shouldReturnByType() throws Exception {
            when(paymentService.findByPaymentType("CASH")).thenReturn(List.of(samplePayment));
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/payments/type/CASH"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/payments/total/sale/{saleId}")
    class GetTotalBySale {

        @Test
        @DisplayName("should return total payments for sale")
        void shouldReturnTotal() throws Exception {
            when(paymentService.getTotalPaymentsBySale(1L)).thenReturn(new BigDecimal("30.00"));

            mockMvc.perform(get("/api/payments/total/sale/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is(30.00)));
        }
    }

    @Nested
    @DisplayName("POST /api/payments")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(paymentMapper.toEntity(any(PaymentDTO.Request.class))).thenReturn(samplePayment);
            when(paymentService.createWithSaleId(any(Payment.class), eq(1L))).thenReturn(samplePayment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when paymentType is invalid")
        void shouldReturn400WhenPaymentTypeInvalid() throws Exception {
            PaymentDTO.Request bad = PaymentDTO.Request.builder()
                    .saleId(1L)
                    .paymentType("BITCOIN")
                    .amount(new BigDecimal("30.00"))
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when amount is negative")
        void shouldReturn400WhenAmountNegative() throws Exception {
            PaymentDTO.Request bad = PaymentDTO.Request.builder()
                    .saleId(1L)
                    .paymentType("CASH")
                    .amount(new BigDecimal("-5.00"))
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when saleId is null")
        void shouldReturn400WhenSaleIdNull() throws Exception {
            PaymentDTO.Request bad = PaymentDTO.Request.builder()
                    .paymentType("CASH")
                    .amount(new BigDecimal("30.00"))
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/payments/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(paymentMapper.toEntity(any(PaymentDTO.Request.class))).thenReturn(samplePayment);
            when(paymentService.findById(1L)).thenReturn(Optional.of(samplePayment));
            when(paymentService.update(eq(1L), any(Payment.class))).thenReturn(samplePayment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/payments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 404 when updating non-existent payment")
        void shouldReturn404WhenNotFound() throws Exception {
            when(paymentMapper.toEntity(any(PaymentDTO.Request.class))).thenReturn(samplePayment);
            when(paymentService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/payments/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/payments/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(paymentService).delete(1L);

            mockMvc.perform(delete("/api/payments/1"))
                    .andExpect(status().isNoContent());

            verify(paymentService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/payments/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(paymentService.count()).thenReturn(20L);

            mockMvc.perform(get("/api/payments/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("20"));
        }
    }
}
