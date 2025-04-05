package de.personal.taskmanager.service;

import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    TaskResponse updateTask(Long id, TaskRequest taskRequest);
    TaskResponse findTaskByIdOrThrow(Long id);
    Page<TaskResponse> getAllTasks(Boolean done, Pageable pageable);
    void deleteTask(Long id);
    TaskResponse markTaskAsDone(Long id);

}
