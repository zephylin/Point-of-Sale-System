package com.pos.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Store entity.
 */
public class StoreDTO {

    /**
     * Request DTO - Used for creating/updating a store.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Store number is required")
        @Size(max = 50, message = "Store number must be at most 50 characters")
        private String number;

        @NotBlank(message = "Store name is required")
        @Size(max = 200, message = "Store name must be at most 200 characters")
        private String name;

        @Size(max = 500, message = "Address must be at most 500 characters")
        private String address;

        @Size(max = 100, message = "City must be at most 100 characters")
        private String city;

        @Size(min = 2, max = 2, message = "State must be exactly 2 characters")
        private String state;

        @Size(max = 10, message = "ZIP code must be at most 10 characters")
        private String zip;

        @Size(max = 20, message = "Phone must be at most 20 characters")
        private String phone;

        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email must be at most 100 characters")
        private String email;

        @Size(max = 100, message = "Manager name must be at most 100 characters")
        private String manager;
    }

    /**
     * Response DTO - Returned by the API.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String number;
        private String name;
        private String address;
        private String city;
        private String state;
        private String zip;
        private String phone;
        private String email;
        private String manager;
        private Boolean isActive;
        private LocalDateTime openedDate;
        private LocalDateTime closedDate;
    }
}
