package de.personal.common.security;

import de.personal.common.util.SecurityErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final SecurityErrorWriter securityErrorWriter;

    public CustomAuthenticationEntryPoint(SecurityErrorWriter securityErrorWriter) {
        this.securityErrorWriter = securityErrorWriter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
