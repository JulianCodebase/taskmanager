package de.personal.taskmanager.dto.task;

import de.personal.taskmanager.model.TaskPriority;
import de.personal.taskmanager.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse extends TaskBase {
    private Long id;
    private TaskPriority priority;
    private TaskStatus status;
}
