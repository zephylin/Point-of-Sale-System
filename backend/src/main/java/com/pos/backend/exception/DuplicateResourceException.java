package com.pos.backend.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * 
 * This results in an HTTP 409 Conflict response.
 * 
 * Example usage:
 *   throw new DuplicateResourceException("Person", "ssn", "123-45-6789");
 *   → "Person already exists with ssn: 123-45-6789"
 */
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public DuplicateResourceException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
