package com.pos.backend.mapper;

import com.pos.backend.domain.Person;
import com.pos.backend.dto.PersonDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Person entity and PersonDTO.
 * 
 * WHY MAPPERS?
 * - Entities have database annotations and internal fields
 * - DTOs are clean objects for the API
 * - Mappers handle the conversion between them
 * 
 * Note: For larger projects, consider using MapStruct library
 * which auto-generates mapper code. For learning purposes,
 * we're writing them manually to understand the concept.
 */
@Component
public class PersonMapper {

    /**
     * Convert a Request DTO to a Person entity.
     * Used when creating or updating a person.
     */
    public Person toEntity(PersonDTO.Request request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAddress(request.getAddress());
        person.setCity(request.getCity());
        person.setState(request.getState());
        person.setZip(request.getZip());
        person.setPhone(request.getPhone());
        person.setSsn(request.getSsn());
        return person;
    }

    /**
     * Convert a Person entity to a Response DTO.
     * Masks the SSN for security.
     */
    public PersonDTO.Response toResponse(Person person) {
        return PersonDTO.Response.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .address(person.getAddress())
                .city(person.getCity())
                .state(person.getState())
                .zip(person.getZip())
                .phone(person.getPhone())
                .maskedSsn(maskSsn(person.getSsn()))
                .build();
    }

    /**
     * Update an existing entity's fields from a Request DTO.
     * Does NOT update the ID or SSN (immutable fields).
     */
    public void updateEntity(Person person, PersonDTO.Request request) {
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAddress(request.getAddress());
        person.setCity(request.getCity());
        person.setState(request.getState());
        person.setZip(request.getZip());
        person.setPhone(request.getPhone());
        // SSN is not updatable for security reasons
    }

    /**
     * Mask SSN to show only last 4 digits.
     * "123-45-6789" → "***-**-6789"
     */
    private String maskSsn(String ssn) {
        if (ssn == null || ssn.length() < 4) {
            return "***-**-****";
        }
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
}
