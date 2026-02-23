package com.pos.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response returned by the API when something goes wrong.
 * 
 * Every error response follows the same structure so the frontend
 * can handle errors consistently.
 * 
 * Example JSON Response:
 * {
 *   "timestamp": "2026-02-23T16:00:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed",
 *   "path": "/api/persons",
 *   "fieldErrors": [
 *     { "field": "firstName", "message": "must not be blank" },
 *     { "field": "ssn", "message": "must match \"\\d{3}-\\d{2}-\\d{4}\"" }
 *   ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't include null fields in JSON
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;

    /**
     * Represents a single field validation error.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
