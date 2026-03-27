package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Item;
import com.pos.backend.dto.ItemDTO;
import com.pos.backend.mapper.ItemMapper;
import com.pos.backend.service.ItemService;
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

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ItemController Integration Tests")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    private Item sampleItem;
    private ItemDTO.Request sampleRequest;
    private ItemDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setNumber("1001");
        sampleItem.setDescription("Turkey Sandwich");
        sampleItem.setPrice(new BigDecimal("2.59"));
        sampleItem.setQuantity(50);
        sampleItem.setIsActive(true);

        sampleRequest = ItemDTO.Request.builder()
                .number("1001")
                .description("Turkey Sandwich")
                .price(new BigDecimal("2.59"))
                .quantity(50)
                .storeId(1L)
                .taxCategoryId(1L)
                .build();

        sampleResponse = ItemDTO.Response.builder()
                .id(1L)
                .number("1001")
                .description("Turkey Sandwich")
                .price(new BigDecimal("2.59"))
                .quantity(50)
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("GET /api/items")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of items")
        void shouldReturnItems() throws Exception {
            when(itemService.findAll()).thenReturn(List.of(sampleItem));
            when(itemMapper.toResponse(any(Item.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/items"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].number", is("1001")))
                    .andExpect(jsonPath("$[0].description", is("Turkey Sandwich")));
        }
    }

    @Nested
    @DisplayName("GET /api/items/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when item found")
        void shouldReturnItem() throws Exception {
            when(itemService.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemMapper.toResponse(sampleItem)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.description", is("Turkey Sandwich")));
        }

        @Test
        @DisplayName("should return 404 when item not found")
        void shouldReturn404() throws Exception {
            when(itemService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/items/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/items/search")
    class Search {

        @Test
        @DisplayName("should return matching items")
        void shouldReturnResults() throws Exception {
            when(itemService.search("Turkey")).thenReturn(List.of(sampleItem));
            when(itemMapper.toResponse(any(Item.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/items/search").param("keyword", "Turkey"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/items/barcode/{barcode}")
    class GetByBarcode {

        @Test
        @DisplayName("should return item by barcode")
        void shouldReturnByBarcode() throws Exception {
            when(itemService.findByBarcode("11111111111")).thenReturn(Optional.of(sampleItem));
            when(itemMapper.toResponse(sampleItem)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/items/barcode/11111111111"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("1001")));
        }

        @Test
        @DisplayName("should return 404 when barcode not found")
        void shouldReturn404() throws Exception {
            when(itemService.findByBarcode("00000000000")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/items/barcode/00000000000"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/items")
    class Create {

        @Test
        @DisplayName("should return 201 when created successfully")
        void shouldCreate() throws Exception {
            when(itemMapper.toEntity(any(ItemDTO.Request.class))).thenReturn(sampleItem);
            when(itemService.createWithIds(any(Item.class), eq(1L), eq(1L))).thenReturn(sampleItem);
            when(itemMapper.toResponse(any(Item.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number", is("1001")));
        }

        @Test
        @DisplayName("should return 400 when description is blank")
        void shouldReturn400WhenDescriptionBlank() throws Exception {
            ItemDTO.Request bad = ItemDTO.Request.builder()
                    .number("1001")
                    .description("")
                    .price(new BigDecimal("2.59"))
                    .quantity(50)
                    .build();

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when price is missing")
        void shouldReturn400WhenPriceMissing() throws Exception {
            ItemDTO.Request bad = ItemDTO.Request.builder()
                    .number("1001")
                    .description("Test")
                    .quantity(1)
                    .build();

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/items/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(itemMapper.toEntity(any(ItemDTO.Request.class))).thenReturn(sampleItem);
            when(itemService.updateWithIds(eq(1L), any(Item.class), eq(1L), eq(1L))).thenReturn(sampleItem);
            when(itemMapper.toResponse(any(Item.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/items/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/items/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(itemService).delete(1L);

            mockMvc.perform(delete("/api/items/1"))
                    .andExpect(status().isNoContent());

            verify(itemService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/items/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(itemService.count()).thenReturn(10L);

            mockMvc.perform(get("/api/items/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("10"));
        }
    }

    @Nested
    @DisplayName("GET /api/items/active")
    class Active {

        @Test
        @DisplayName("should return active items")
        void shouldReturnActive() throws Exception {
            when(itemService.findAllActive()).thenReturn(List.of(sampleItem));
            when(itemMapper.toResponse(any(Item.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/items/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }
}
