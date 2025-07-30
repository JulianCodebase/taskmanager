package de.personal.userservice.dto;

import de.personal.userservice.model.UserRole;

public record AuthResponse(
        String username,
        UserRole role, // USER or ADMIN
        String message
) {
}
