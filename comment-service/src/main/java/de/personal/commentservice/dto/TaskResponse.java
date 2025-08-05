package de.personal.commentservice.dto;

public record TaskResponse(
        Long id,
        String title,
        boolean done
) {
}
