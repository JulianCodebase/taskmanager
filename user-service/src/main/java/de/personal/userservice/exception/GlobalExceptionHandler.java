package de.personal.userservice.exception;

import de.personal.taskmanager.common.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
 *
 * <p> This class catches exceptions thrown by controllers or services
 * and converts them into appropriate HTTP responses.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SecurityErrorWriter securityErrorWriter;

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public void handleAccessErrors(HttpServletResponse response,
                                   Exception exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class, TaskNotFoundException.class})
    public void handleNotFoundErrors(HttpServletResponse response,
                                     Exception exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(HttpServletResponse response,
                                      IllegalArgumentException exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions(HttpServletResponse response,
                                           MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : exception.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        securityErrorWriter.writeJson(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage(), fieldErrors);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException and returns a 405 Method Not Allowed response.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleMethodNotSupportedException(HttpServletResponse response,
                                                  HttpRequestMethodNotSupportedException exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, exception.getMessage());
    }

    /**
     * Handles all uncaught exceptions and returns a 500 Internal Server Error response with a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public void handleGenericExceptions(HttpServletResponse response,
                                        Exception exception) {
        log.error("🛑 {}", exception.getMessage(), exception);

        securityErrorWriter.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
