package de.personal.taskmanager.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskCompletedEvent extends ApplicationEvent {
    private final Long taskId;

    public TaskCompletedEvent(Object source, Long taskId) {
        super(source);
        this.taskId = taskId;
    }

}
