package de.personal.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.common.dto.CustomErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Writes consistent JSON error responses for security-related exceptions.
 */
public record SecurityErrorWriter(ObjectMapper objectMapper) {

    public void writeJson(HttpServletResponse response, int status, String message) {
        writeJson(response, status, message, null);
    }

    public void writeJson(HttpServletResponse response, int status, String message, Map<String, String> fieldErrors) {
        response.setStatus(status);
        response.setContentType("application/json");
        CustomErrorResponse body = new CustomErrorResponse(status, message, fieldErrors);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
