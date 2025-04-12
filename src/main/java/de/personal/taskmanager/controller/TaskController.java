package de.personal.taskmanager.controller;

import de.personal.taskmanager.annotation.AuditLog;
import de.personal.taskmanager.dto.task.TaskRequest;
import de.personal.taskmanager.dto.task.TaskResponse;
import de.personal.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @AuditLog(desc = "Creating a task")
    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
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
        return ResponseEntity.ok(taskService.findTaskByIdOrThrow(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> markTaskAsDone(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskAsDone(id));
    }

    @AuditLog(desc = "Deleting a task")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
