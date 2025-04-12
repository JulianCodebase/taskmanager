package de.personal.taskmanager.util;

import de.personal.taskmanager.dto.task.TaskRequest;
import de.personal.taskmanager.dto.task.TaskResponse;
import de.personal.taskmanager.model.Task;

public class TaskMapper {
    public static Task toTaskEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setDone(false);
        return task;
    }

    public static TaskResponse toTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDueDate(task.getDueDate());
        taskResponse.setDone(task.getDone());
        return taskResponse;
    }
}
