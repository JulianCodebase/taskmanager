package de.personal.taskmanager.service.impl;

import de.personal.taskmanager.common.CommentMapper;
import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.model.TaskComment;
import de.personal.taskmanager.respository.TaskCommentRepository;
import de.personal.taskmanager.respository.TaskRepository;
import de.personal.taskmanager.respository.UserRepository;
import de.personal.taskmanager.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {
    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskCommentResponse addComment(TaskCommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + request.getTaskId()));

        TaskComment comment = CommentMapper.toTaskComment(request, task);
        TaskComment saved = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(saved);
    }

    @Override
    public Page<TaskCommentResponse> getFilteredComments(Long taskId, String keyword, LocalDate after, LocalDate before, Pageable pageable) {
        // Convert LocalDate to LocalDateTime to match DB columns' format
        // If no filter provided, return last 30-day comments
        LocalDateTime afterDateTime = (after != null)
                ? after.atStartOfDay()
                : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime beforeDateTime = (before != null)
                ? before.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return commentRepository.findFilteredComments(taskId, keyword, afterDateTime, beforeDateTime, pageable)
                .map(CommentMapper::toCommentResponse);
    }

    @Override
    public void deleteComment(Long commentId) {
        TaskComment comment = getOwnedComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public TaskCommentResponse updateComment(Long commentId, TaskCommentRequest request) {
        TaskComment comment = getOwnedComment(commentId);
        comment.setContent(request.getContent());
        TaskComment updated = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(updated);
    }

    @NotNull
    private TaskComment getOwnedComment(Long commentId) {
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!currentUsername.equals(comment.getAuthor().getUsername())) {
            throw new AccessDeniedException("Operation forbidden because you're not the author");
        }

        return comment;
    }
}
