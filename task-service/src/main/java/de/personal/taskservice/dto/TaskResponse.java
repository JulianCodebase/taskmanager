package de.personal.taskservice.dto;

import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse extends TaskBase {
    private Long id;
    private TaskPriority priority;
    private TaskStatus status;
}
