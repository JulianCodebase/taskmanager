package de.personal.taskmanager.dto.task;


import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class TaskRequest extends TaskBase {
    private Boolean done = false;
}