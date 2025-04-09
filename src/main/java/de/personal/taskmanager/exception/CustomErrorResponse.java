package de.personal.taskmanager.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private Map<String, String> errors;

    public CustomErrorResponse(int status, String message, Map<String, String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
