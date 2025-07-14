package de.personal.taskmanager.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a structured error response returned to the client.
 * <p>
 * This class is used by both global and security exception handlers to send consistent
 * JSON responses with status code, message, optional field-level errors, and a timestamp.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Tell Jackson to exclude null property from the serialized JSON
public class CustomErrorResponse {
    private int status;
    private String message;
    private Map<String, String> errors; // Optional field-specific validation errors (can be null)
    private LocalDateTime timestamp;

    public CustomErrorResponse(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
