package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Store;
import com.pos.backend.dto.StoreDTO;
import com.pos.backend.mapper.StoreMapper;
import com.pos.backend.service.StoreService;
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

@WebMvcTest(StoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("StoreController Integration Tests")
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private StoreService storeService;

    @MockBean
    private StoreMapper storeMapper;

    private Store sampleStore;
    private StoreDTO.Request sampleRequest;
    private StoreDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleStore = new Store();
        sampleStore.setId(1L);
        sampleStore.setNumber("S001");
        sampleStore.setName("Quick Mart");
        sampleStore.setCity("Edmond");
        sampleStore.setState("OK");
        sampleStore.setIsActive(true);

        sampleRequest = StoreDTO.Request.builder()
                .number("S001")
                .name("Quick Mart")
                .city("Edmond")
                .state("OK")
                .build();

        sampleResponse = StoreDTO.Response.builder()
                .id(1L)
                .number("S001")
                .name("Quick Mart")
                .city("Edmond")
                .state("OK")
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("GET /api/stores")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of stores")
        void shouldReturnStores() throws Exception {
            when(storeService.findAll()).thenReturn(List.of(sampleStore));
            when(storeMapper.toResponse(any(Store.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/stores"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].number", is("S001")))
                    .andExpect(jsonPath("$[0].name", is("Quick Mart")));
        }
    }

    @Nested
    @DisplayName("GET /api/stores/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when store found")
        void shouldReturnStore() throws Exception {
            when(storeService.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(storeMapper.toResponse(sampleStore)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/stores/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.number", is("S001")));
        }

        @Test
        @DisplayName("should return 404 when store not found")
        void shouldReturn404() throws Exception {
            when(storeService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/stores/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/stores/search")
    class Search {

        @Test
        @DisplayName("should return matching stores")
        void shouldReturnResults() throws Exception {
            when(storeService.searchByName("Quick")).thenReturn(List.of(sampleStore));
            when(storeMapper.toResponse(any(Store.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/stores/search").param("name", "Quick"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("POST /api/stores")
    class Create {

        @Test
        @DisplayName("should return 201 when created successfully")
        void shouldCreate() throws Exception {
            when(storeMapper.toEntity(any(StoreDTO.Request.class))).thenReturn(sampleStore);
            when(storeService.create(any(Store.class))).thenReturn(sampleStore);
            when(storeMapper.toResponse(any(Store.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/stores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number", is("S001")));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            StoreDTO.Request bad = StoreDTO.Request.builder()
                    .number("S001")
                    .name("")
                    .build();

            mockMvc.perform(post("/api/stores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when number is missing")
        void shouldReturn400WhenNumberMissing() throws Exception {
            StoreDTO.Request bad = StoreDTO.Request.builder()
                    .name("Some Store")
                    .build();

            mockMvc.perform(post("/api/stores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/stores/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(storeMapper.toEntity(any(StoreDTO.Request.class))).thenReturn(sampleStore);
            when(storeService.update(eq(1L), any(Store.class))).thenReturn(sampleStore);
            when(storeMapper.toResponse(any(Store.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/stores/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Quick Mart")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/stores/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(storeService).delete(1L);

            mockMvc.perform(delete("/api/stores/1"))
                    .andExpect(status().isNoContent());

            verify(storeService).delete(1L);
        }
    }

    @Nested
    @DisplayName("PATCH /api/stores/{id}/deactivate")
    class Deactivate {

        @Test
        @DisplayName("should deactivate store")
        void shouldDeactivate() throws Exception {
            sampleResponse.setIsActive(false);
            when(storeService.deactivate(1L)).thenReturn(sampleStore);
            when(storeMapper.toResponse(any(Store.class))).thenReturn(sampleResponse);

            mockMvc.perform(patch("/api/stores/1/deactivate"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive", is(false)));
        }
    }

    @Nested
    @DisplayName("GET /api/stores/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(storeService.count()).thenReturn(5L);

            mockMvc.perform(get("/api/stores/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }
    }
}
