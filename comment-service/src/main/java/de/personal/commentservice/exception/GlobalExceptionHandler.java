package de.personal.commentservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.common.util.SecurityErrorWriter;
import feign.FeignException;
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
    private final ObjectMapper objectMapper;

    @ExceptionHandler(FeignException.NotFound.class)
    public void handleNotFoundErrors(HttpServletResponse response,
                                     FeignException exception) {
        securityErrorWriter.writeJson(response, HttpServletResponse.SC_NOT_FOUND, extractMessage(exception));
    }

    // Gets the raw response body (as a UTF-8 string) returned from the remote service
    private String extractMessage(FeignException ex) {
        try {
            return objectMapper.readTree(ex.contentUTF8()).get("message").asText();
        } catch (Exception e) {
            return "Task not found";
        }
    }
}
