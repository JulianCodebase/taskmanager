package de.personal.taskservice.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler({UsernameNotFoundException.class, TaskNotFoundException.class})
    public void handleNotFoundErrors(HttpServletResponse response,
                                     Exception exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }
}
