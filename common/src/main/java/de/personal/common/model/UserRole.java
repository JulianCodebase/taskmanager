package de.personal.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// UserRole is parse across multiple services for security decisions, meaning it must be shared
// The value must start with "ROLE_" to comply with Spring Security conventions (e.g., "ROLE_USER", "ROLE_ADMIN").
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    /**
     * When Jackson sees the @JsonCreator method, it uses it instead of the default valueOf()
     */
    @JsonCreator
    public static UserRole fromString(String value) {
        return switch (value.toUpperCase()) {
            case "USER" -> ROLE_USER;
            case "ADMIN" -> ROLE_ADMIN;
            default -> throw new IllegalArgumentException("Role must be either USER or ADMIN (case-insensitive).");
        };
    }

    /**
     * Serialize the enum value as a simplified role name without the "ROLE_" prefix.
     * Used during JSON serialization when sending data to clients.
     *
     * @return the simplified role name (e.g., "USER" or "ADMIN")
     */
    @JsonValue
    public String toReadableJson() {
        return this.name().replace("ROLE_", "");
    }
}
