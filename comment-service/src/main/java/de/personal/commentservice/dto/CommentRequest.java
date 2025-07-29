package de.personal.commentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the payload to create a task comment.
 */
@Getter
@Setter
public class CommentRequest {
    @NotBlank
    private String content;

    @NotNull
    private Long taskId;
}
