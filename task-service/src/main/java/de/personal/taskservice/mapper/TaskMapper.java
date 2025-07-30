package de.personal.taskservice.mapper;


import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.model.Task;

public class TaskMapper {
    public static Task toTaskEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        task.setStatus(taskRequest.getStatus());
        return task;
    }

    public static TaskResponse toTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDueDate(task.getDueDate());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setStatus(task.getStatus());
        return taskResponse;
    }
}
