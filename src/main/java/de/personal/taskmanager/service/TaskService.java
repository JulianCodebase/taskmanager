package de.personal.taskmanager.service;

import de.personal.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    Task updateTask(Long id, Task newTask);
    Optional<Task> findTaskById(Long id);
    List<Task> getAllTasks();
    void deleteTask(Long id);
    Task markTaskAsDone(Long id);

}
