package de.personal.commentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the payload to create a task comment.
 */
public record CommentRequest(
        @NotBlank
        String content,

        @NotNull
        Long taskId
) {
}
