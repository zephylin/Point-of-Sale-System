package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.TaxCategory;
import com.pos.backend.dto.TaxCategoryDTO;
import com.pos.backend.mapper.TaxCategoryMapper;
import com.pos.backend.service.TaxCategoryService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TaxCategoryController Integration Tests")
class TaxCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TaxCategoryService taxCategoryService;

    @MockBean
    private TaxCategoryMapper taxCategoryMapper;

    private TaxCategory sampleTaxCategory;
    private TaxCategoryDTO.Request sampleRequest;
    private TaxCategoryDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleTaxCategory = new TaxCategory();
        sampleTaxCategory.setId(1L);
        sampleTaxCategory.setCategory("Food");
        sampleTaxCategory.setDescription("Food items");
        sampleTaxCategory.setIsActive(true);

        sampleRequest = TaxCategoryDTO.Request.builder()
                .category("Food")
                .description("Food items")
                .build();

        sampleResponse = TaxCategoryDTO.Response.builder()
                .id(1L)
                .category("Food")
                .description("Food items")
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("GET /api/tax-categories")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of tax categories")
        void shouldReturnTaxCategories() throws Exception {
            when(taxCategoryService.findAll()).thenReturn(List.of(sampleTaxCategory));
            when(taxCategoryMapper.toResponse(any(TaxCategory.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].category", is("Food")));
        }
    }

    @Nested
    @DisplayName("GET /api/tax-categories/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturnTaxCategory() throws Exception {
            when(taxCategoryService.findById(1L)).thenReturn(Optional.of(sampleTaxCategory));
            when(taxCategoryMapper.toResponse(sampleTaxCategory)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-categories/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.category", is("Food")));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(taxCategoryService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/tax-categories/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/tax-categories/category/{category}")
    class GetByName {

        @Test
        @DisplayName("should return tax category by name")
        void shouldReturnByName() throws Exception {
            when(taxCategoryService.findByCategory("Food")).thenReturn(Optional.of(sampleTaxCategory));
            when(taxCategoryMapper.toResponse(sampleTaxCategory)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-categories/category/Food"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.category", is("Food")));
        }
    }

    @Nested
    @DisplayName("GET /api/tax-categories/search")
    class Search {

        @Test
        @DisplayName("should return matching results")
        void shouldReturnResults() throws Exception {
            when(taxCategoryService.search("Food")).thenReturn(List.of(sampleTaxCategory));
            when(taxCategoryMapper.toResponse(any(TaxCategory.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-categories/search").param("keyword", "Food"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/tax-categories/active")
    class GetActive {

        @Test
        @DisplayName("should return active tax categories")
        void shouldReturnActive() throws Exception {
            when(taxCategoryService.findAllActive()).thenReturn(List.of(sampleTaxCategory));
            when(taxCategoryMapper.toResponse(any(TaxCategory.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/tax-categories/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isActive", is(true)));
        }
    }

    @Nested
    @DisplayName("POST /api/tax-categories")
    class Create {

        @Test
        @DisplayName("should return 201 when created")
        void shouldCreate() throws Exception {
            when(taxCategoryMapper.toEntity(any(TaxCategoryDTO.Request.class))).thenReturn(sampleTaxCategory);
            when(taxCategoryService.create(any(TaxCategory.class))).thenReturn(sampleTaxCategory);
            when(taxCategoryMapper.toResponse(any(TaxCategory.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/tax-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.category", is("Food")));
        }

        @Test
        @DisplayName("should return 400 when category is blank")
        void shouldReturn400WhenCategoryBlank() throws Exception {
            TaxCategoryDTO.Request bad = TaxCategoryDTO.Request.builder()
                    .category("")
                    .build();

            mockMvc.perform(post("/api/tax-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/tax-categories/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(taxCategoryMapper.toEntity(any(TaxCategoryDTO.Request.class))).thenReturn(sampleTaxCategory);
            when(taxCategoryService.update(eq(1L), any(TaxCategory.class))).thenReturn(sampleTaxCategory);
            when(taxCategoryMapper.toResponse(any(TaxCategory.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/tax-categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/tax-categories/{id}/deactivate")
    class Deactivate {

        @Test
        @DisplayName("should return 200 when deactivated")
        void shouldDeactivate() throws Exception {
            when(taxCategoryService.deactivate(1L)).thenReturn(sampleTaxCategory);
            when(taxCategoryMapper.toResponse(sampleTaxCategory)).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/tax-categories/1/deactivate"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/tax-categories/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(taxCategoryService).delete(1L);

            mockMvc.perform(delete("/api/tax-categories/1"))
                    .andExpect(status().isNoContent());

            verify(taxCategoryService).delete(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/tax-categories/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(taxCategoryService.count()).thenReturn(4L);

            mockMvc.perform(get("/api/tax-categories/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("4"));
        }
    }
}
