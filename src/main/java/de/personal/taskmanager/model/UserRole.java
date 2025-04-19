package de.personal.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    USER,
    ADMIN;

    // When Jackson sees the @JsonCreator method, it uses it instead of the default valueOf()
    @JsonCreator
    public static UserRole fromString(String value) {
        return switch (value.toUpperCase()) {
            case "USER" -> USER;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Role must be either USER or ADMIN (case-insensitive).");
        };
    }
}
