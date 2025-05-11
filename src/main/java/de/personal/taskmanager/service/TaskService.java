package de.personal.taskmanager.service;

import de.personal.taskmanager.dto.task.TaskRequest;
import de.personal.taskmanager.dto.task.TaskResponse;
import de.personal.taskmanager.model.AppUser;
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

    TaskResponse markTaskAsDone(Long id, AppUser user);

    void forceDeleteTask(Long id);
}
