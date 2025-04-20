package de.personal.taskmanager.service;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TaskCommentService {
    TaskCommentResponse addComment(TaskCommentRequest request);

    Page<TaskCommentResponse> getFilteredComments(Long taskId, String keyword, LocalDate after, LocalDate before, Pageable pageable);

    void deleteComment(Long commentId);

    TaskCommentResponse updateComment(Long commentId, TaskCommentRequest request);
}
