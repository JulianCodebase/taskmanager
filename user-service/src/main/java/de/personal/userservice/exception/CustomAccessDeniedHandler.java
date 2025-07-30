package de.personal.userservice.exception;

import de.personal.userservice.security.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Custom handler to return structured JSON for 403 responses.
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityErrorWriter securityErrorWriter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    }
}
