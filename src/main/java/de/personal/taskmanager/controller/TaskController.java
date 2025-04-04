package de.personal.taskmanager.controller;

import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import de.personal.taskmanager.exception.TaskNotFoundException;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.service.TaskService;
import de.personal.taskmanager.util.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Task task = TaskMapper.toTaskEntity(taskRequest);
        Task saved = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskMapper.toTaskResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> taskResponses = tasks.stream().map(TaskMapper::toTaskResponse).toList();
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getSingleTask(@PathVariable Long id) {
        Task task = taskService.findTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return ResponseEntity.ok(TaskMapper.toTaskResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest taskRequest) {
        Task updatedTask = taskService.updateTask(id, TaskMapper.toTaskEntity(taskRequest));
        return ResponseEntity.ok(TaskMapper.toTaskResponse(updatedTask));
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<TaskResponse> markTaskAsDone(@PathVariable Long id) {
        Task completedTask = taskService.markTaskAsDone(id);
        return ResponseEntity.ok(TaskMapper.toTaskResponse(completedTask));
    }
}
