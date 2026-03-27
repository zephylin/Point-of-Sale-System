package com.pos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.backend.domain.Person;
import com.pos.backend.dto.PersonDTO;
import com.pos.backend.mapper.PersonMapper;
import com.pos.backend.service.PersonService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PersonController Integration Tests")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PersonService personService;

    @MockBean
    private PersonMapper personMapper;

    private Person samplePerson;
    private PersonDTO.Request sampleRequest;
    private PersonDTO.Response sampleResponse;

    @BeforeEach
    void setUp() {
        samplePerson = new Person();
        samplePerson.setId(1L);
        samplePerson.setFirstName("David");
        samplePerson.setLastName("Martin");
        samplePerson.setCity("Edmond");
        samplePerson.setState("OK");
        samplePerson.setSsn("111-11-1111");

        sampleRequest = PersonDTO.Request.builder()
                .firstName("David")
                .lastName("Martin")
                .city("Edmond")
                .state("OK")
                .ssn("111-11-1111")
                .build();

        sampleResponse = PersonDTO.Response.builder()
                .id(1L)
                .firstName("David")
                .lastName("Martin")
                .city("Edmond")
                .state("OK")
                .maskedSsn("***-**-1111")
                .build();
    }

    @Nested
    @DisplayName("GET /api/persons")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list of persons")
        void shouldReturnPersons() throws Exception {
            when(personService.getAllPersons()).thenReturn(List.of(samplePerson));
            when(personMapper.toResponse(any(Person.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/persons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].firstName", is("David")))
                    .andExpect(jsonPath("$[0].maskedSsn", is("***-**-1111")));
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when person found")
        void shouldReturnPerson() throws Exception {
            when(personService.getPersonById(1L)).thenReturn(samplePerson);
            when(personMapper.toResponse(samplePerson)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/persons/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstName", is("David")));
        }
    }

    @Nested
    @DisplayName("GET /api/persons/search")
    class Search {

        @Test
        @DisplayName("should return matching persons")
        void shouldReturnResults() throws Exception {
            when(personService.searchPersonsByName("David")).thenReturn(List.of(samplePerson));
            when(personMapper.toResponse(any(Person.class))).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/persons/search").param("name", "David"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("POST /api/persons")
    class Create {

        @Test
        @DisplayName("should return 201 when created successfully")
        void shouldCreate() throws Exception {
            when(personMapper.toEntity(any(PersonDTO.Request.class))).thenReturn(samplePerson);
            when(personService.createPerson(any(Person.class))).thenReturn(samplePerson);
            when(personMapper.toResponse(any(Person.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/persons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName", is("David")));
        }

        @Test
        @DisplayName("should return 400 when first name is blank")
        void shouldReturn400WhenFirstNameBlank() throws Exception {
            PersonDTO.Request bad = PersonDTO.Request.builder()
                    .firstName("")
                    .lastName("Martin")
                    .build();

            mockMvc.perform(post("/api/persons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when last name is missing")
        void shouldReturn400WhenLastNameMissing() throws Exception {
            PersonDTO.Request bad = PersonDTO.Request.builder()
                    .firstName("David")
                    .build();

            mockMvc.perform(post("/api/persons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when SSN format is invalid")
        void shouldReturn400WhenSsnInvalid() throws Exception {
            PersonDTO.Request bad = PersonDTO.Request.builder()
                    .firstName("David")
                    .lastName("Martin")
                    .ssn("12345")
                    .build();

            mockMvc.perform(post("/api/persons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/persons/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updated")
        void shouldUpdate() throws Exception {
            when(personMapper.toEntity(any(PersonDTO.Request.class))).thenReturn(samplePerson);
            when(personService.updatePerson(eq(1L), any(Person.class))).thenReturn(samplePerson);
            when(personMapper.toResponse(any(Person.class))).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/persons/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/persons/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldDelete() throws Exception {
            doNothing().when(personService).deletePerson(1L);

            mockMvc.perform(delete("/api/persons/1"))
                    .andExpect(status().isNoContent());

            verify(personService).deletePerson(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/persons/count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() throws Exception {
            when(personService.countPersons()).thenReturn(3L);

            mockMvc.perform(get("/api/persons/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("3"));
        }
    }
}
