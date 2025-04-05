package de.personal.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Task Manager application.
 * <p> This class catches exceptions thrown by controllers or services
 * and converts them into appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors (MethodArgumentNotValidException) and returns a 400 Bad Request
     * with detailed field error messages in a structured format.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : exception.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles TaskNotFoundException and returns a 404 Not Found response with the exception message.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFound(TaskNotFoundException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles all uncaught exceptions and returns a 500 Internal Server Error response with a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericExceptions(Exception exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException and returns a 405 Method Not Allowed response.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomErrorResponse>
    handleMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Request method not supported",
                null
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
}
