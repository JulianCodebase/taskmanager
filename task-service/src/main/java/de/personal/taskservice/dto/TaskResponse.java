package de.personal.taskservice.dto;

import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskResponse(
        Long id,

        @NotBlank(message = "Title is mandatory")
        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        TaskPriority priority,
        TaskStatus status,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate
) {
}
