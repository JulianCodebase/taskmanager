package de.personal.taskservice.service;

import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);

    TaskResponse updateTask(Long id, TaskRequest taskRequest);

    TaskResponse findTaskByIdOrThrow(Long id);

    Page<TaskResponse> getAllActiveTasks(Pageable pageable);

    void deleteTask(Long id);

    TaskResponse restoreTask(Long id);

    int restoreAllSoftDeletedTasks();

    TaskResponse markTaskAsDone(Long id, String username);

    void forceDeleteTask(Long id);
}
