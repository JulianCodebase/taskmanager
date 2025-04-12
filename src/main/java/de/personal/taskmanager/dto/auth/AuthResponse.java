package de.personal.taskmanager.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private String role; // USER or ADMIN
    private String message;
}
