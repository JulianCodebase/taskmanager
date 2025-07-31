package de.personal.taskservice.controller;

import de.personal.taskservice.annotation.AuditLog;
import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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
            @PageableDefault(size = 20, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllActiveTasks(pageable));
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
    public ResponseEntity<TaskResponse> markTaskAsDone(@PathVariable Long id,
                                                       @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        return ResponseEntity.ok(taskService.markTaskAsDone(id, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<TaskResponse> restoreTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.restoreTask(id));
    }

    @PatchMapping("/restore")
    public ResponseEntity<Map<String, Object>> restoreAllSoftDeletedTasks() {
        int restoredCount = taskService.restoreAllSoftDeletedTasks();
        Map<String, Object> response = Map.of(
                "restoredCount", restoredCount,
                "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/force")
    public ResponseEntity<Void> forceDeleteTask(@PathVariable Long id) {
        taskService.forceDeleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
