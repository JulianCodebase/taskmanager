package de.personal.taskservice.exception;

import de.personal.common.util.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SecurityErrorWriter securityErrorWriter;

    @ExceptionHandler(TaskNotFoundException.class)
    public void handleNotFoundErrors(HttpServletResponse response,
                                     Exception exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }
}
