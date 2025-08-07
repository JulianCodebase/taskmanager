package de.personal.commentservice.service;

import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CommentService {
    CommentResponse addComment(CommentRequest request);

    Page<CommentResponse> getFilteredComments(Long taskId, String keyword, LocalDate after, LocalDate before, Pageable pageable);

    void deleteComment(Long commentId);

    CommentResponse updateComment(Long commentId, CommentRequest request);

    int deleteCommentsByTaskId(Long taskId);
}
