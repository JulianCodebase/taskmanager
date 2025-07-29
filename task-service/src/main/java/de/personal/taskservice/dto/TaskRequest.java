package de.personal.taskservice.dto;


import de.personal.taskmanager.model.TaskPriority;
import de.personal.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskRequest extends TaskBase {
    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    @NotNull(message = "Task status is required")
    private TaskStatus status;
}