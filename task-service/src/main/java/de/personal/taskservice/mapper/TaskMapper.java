package de.personal.taskservice.mapper;


import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.model.Task;

public class TaskMapper {
    public static Task toTaskEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setDueDate(taskRequest.dueDate());
        task.setPriority(taskRequest.priority());
        task.setStatus(taskRequest.status());
        return task;
    }

    public static TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate()
        );
    }
}
