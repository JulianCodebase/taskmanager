package de.personal.taskmanager.controller;

import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import de.personal.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        log.info("Creating task: {}", taskRequest.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskRequest));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) Boolean done,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(done, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getSingleTask(@PathVariable Long id) {
        log.info("Fetching task with id: {}", id);
        return ResponseEntity.ok(taskService.findTaskByIdOrThrow(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest taskRequest) {
        log.debug("Updating a task with id: {}, new task: {}", id, taskRequest.getTitle());
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> markTaskAsDone(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskAsDone(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting a task with id: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
