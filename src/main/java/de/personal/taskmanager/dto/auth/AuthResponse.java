package de.personal.taskmanager.dto.auth;

import de.personal.taskmanager.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private UserRole role; // USER or ADMIN
    private String message;
}
