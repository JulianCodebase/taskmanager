package de.personal.userservice.dto;

import de.personal.common.model.UserRole;

public record AuthResponse(
        String username,
        UserRole role, // USER or ADMIN
        String message
) {
}
