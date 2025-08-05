package de.personal.taskservice.exception;

import de.personal.common.util.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SecurityErrorWriter securityErrorWriter;

    @ExceptionHandler(TaskNotFoundException.class)
    public void handleNotFoundErrors(HttpServletResponse response,
                                     Exception exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
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
