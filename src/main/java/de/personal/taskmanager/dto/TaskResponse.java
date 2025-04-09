package de.personal.taskmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Boolean done;
}
