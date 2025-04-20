package de.personal.taskmanager.controller;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import de.personal.taskmanager.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
     * Retrieves filtered comments
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<TaskCommentResponse>> getCommentsByTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(commentService.getFilteredComments(taskId, keyword, after, before, pageable));
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
