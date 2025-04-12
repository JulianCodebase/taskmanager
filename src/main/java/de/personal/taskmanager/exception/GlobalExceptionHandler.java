package de.personal.taskmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "No permission to access this resource",
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUsernameNotFound(UsernameNotFoundException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all uncaught exceptions and returns a 500 Internal Server Error response with a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericExceptions(Exception exception) {
        log.error("🛑 {}", exception.getMessage(), exception);

        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
