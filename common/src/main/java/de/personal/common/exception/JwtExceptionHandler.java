package de.personal.common.exception;

import de.personal.common.util.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Slf4j
public class JwtExceptionHandler {
    private final SecurityErrorWriter securityErrorWriter;

    public JwtExceptionHandler(SecurityErrorWriter securityErrorWriter) {
        this.securityErrorWriter = securityErrorWriter;
    }

    public void handleInvalidToken(HttpServletResponse response, Exception e) {
        log.error(">>> Invalid JWT: {}", e.getMessage());
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public void handleUserNotFound(HttpServletResponse response, UsernameNotFoundException e) {
        log.error(">>> Username not found: {}", e.getMessage());
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }


}
