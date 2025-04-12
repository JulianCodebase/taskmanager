package de.personal.taskmanager.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true) // Include parent information in `toString`
public class AuthRegisterRequest extends AuthRequest {
    @NotBlank
    private String role; // USER or ADMIN
}
