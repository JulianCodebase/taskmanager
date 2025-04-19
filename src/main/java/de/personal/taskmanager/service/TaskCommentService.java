package de.personal.taskmanager.service;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;

import java.util.List;

public interface TaskCommentService {
    TaskCommentResponse addComment(TaskCommentRequest request);

    List<TaskCommentResponse> getCommentsByTaskId(Long taskId);

    void deleteComment(Long commentId);

    TaskCommentResponse updateComment(Long commentId, TaskCommentRequest request);
}
