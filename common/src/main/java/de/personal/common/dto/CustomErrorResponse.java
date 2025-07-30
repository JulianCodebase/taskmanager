package de.personal.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * Represents a structured error response returned to the client.
 * Used by exception handlers to return JSON errors with status, message, field-level errors, and timestamp.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Tell Jackson to exclude null property from the serialized JSON
public record CustomErrorResponse(
        int status,
        String message,
        Map<String, String> errors, // Optional field-specific validation errors (can be null)
        LocalDateTime timestamp
) {
    public CustomErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message, errors, LocalDateTime.now());
    }

    public CustomErrorResponse() {
        this(1, "", null, LocalDateTime.now());
    }
}
