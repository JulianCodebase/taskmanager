package de.personal.taskmanager.controller;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import de.personal.taskmanager.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class TaskCommentController {
    private final TaskCommentService commentService;

    /**
     * Creates a new comment on a task
     */
    @PostMapping
    public ResponseEntity<TaskCommentResponse> addComment(@RequestBody @Valid TaskCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(request));
    }

    /**
     * Retrieves all comments for a specific task
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskCommentResponse>> getComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCommentResponse> updateComment(@PathVariable("id") Long commentId,
                                                             @RequestBody TaskCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }
}
