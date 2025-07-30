package de.personal.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.personal.userservice.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @JsonProperty("role") UserRole userRole // USER or ADMIN
) {
}
