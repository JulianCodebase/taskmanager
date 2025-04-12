package de.personal.taskmanager.dto.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse extends TaskBase {
    private Long id;
    private Boolean done;
}
