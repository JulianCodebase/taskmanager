package de.personal.userservice.exception;

import de.personal.common.util.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtExceptionHandler {
    private final SecurityErrorWriter securityErrorWriter;

    public void handleInvalidToken(HttpServletResponse response, Exception e) {
        log.error(">>> Invalid JWT: {}", e.getMessage());
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public void handleUserNotFound(HttpServletResponse response, UsernameNotFoundException e) {
        log.error(">>> Username not found: {}", e.getMessage());
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }


}
