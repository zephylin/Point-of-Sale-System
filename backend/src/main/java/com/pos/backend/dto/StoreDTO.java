package com.pos.backend.dto;

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
        private String number;
        private String name;
        private String address;
        private String city;
        private String state;
        private String zip;
        private String phone;
        private String email;
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
