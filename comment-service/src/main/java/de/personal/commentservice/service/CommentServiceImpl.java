package de.personal.commentservice.service;

import de.personal.commentservice.client.TaskClient;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import de.personal.commentservice.mapper.CommentMapper;
import de.personal.commentservice.model.Comment;
import de.personal.commentservice.repository.CommentRepository;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskClient taskClient;

    @Override
    public CommentResponse addComment(CommentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskClient.ensureTaskExists(request.taskId());

        Comment comment = CommentMapper.toComment(request);
        comment.setAuthorUsername(username);

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(saved);
    }

    @Override
    public Page<CommentResponse> getFilteredComments(Long taskId, String keyword, LocalDate after, LocalDate before, Pageable pageable) {
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
        Comment comment = getOwnedComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = getOwnedComment(commentId);
        comment.setContent(request.content());
        Comment updated = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(updated);
    }

    @NotNull
    private Comment getOwnedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!currentUsername.equals(comment.getAuthorUsername())) {
            throw new AccessDeniedException("Operation forbidden because you're not the author");
        }

        return comment;
    }
}
