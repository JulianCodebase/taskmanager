package de.personal.commentservice.service;

import de.personal.commentservice.client.TaskClient;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import de.personal.commentservice.dto.TaskResponse;
import de.personal.commentservice.mapper.CommentMapper;
import de.personal.commentservice.model.Comment;
import de.personal.commentservice.repository.CommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskClient taskClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentRequest request;

    @BeforeEach
    void setUp() {
        request = new CommentRequest("content", 1L);

        // Mock authentication context
        Authentication authentication = mock(Authentication.class);
        // Use lenient() to tell Mockito don't throw an exception
        // even if this stub isn't used in a test
        lenient().when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addComment() {
        // Arrange
        Comment comment = CommentMapper.toComment(request);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentResponse response = commentService.addComment(request);

        // Assertions
        assertEquals(request.content(), response.content());
    }

    @Test
    void addComment_shouldVerifyTaskExists() {
        // Arrange
        CommentRequest request = new CommentRequest("Test comment", 1L);
        TaskResponse taskResponse = new TaskResponse(1L, "Sample Task", false);

        doNothing().when(taskClient).ensureTaskExists(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setTaskId(1L);
        comment.setContent("Test comment");

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentResponse response = commentService.addComment(request);

        // Assertions
        assertEquals(1L, response.id());
        verify(taskClient).ensureTaskExists(1L); // ensure it was called
    }

    @Test
    void getFilteredComments_shouldReturnFilteredResults() {
        // Arrange
        Long taskId = 1L;
        String keyword = "important";
        LocalDate after = LocalDate.now().minusDays(10);
        LocalDate before = LocalDate.now();
        Pageable pageable = Pageable.ofSize(10);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setTaskId(taskId);
        comment.setContent("important content");
        comment.setCreatedAt(LocalDateTime.now().minusDays(5));
        comment.setAuthorUsername("testuser");

        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        when(commentRepository.findFilteredComments(any(), any(), any(), any(), any()))
                .thenReturn(commentPage);

        // Act
        Page<CommentResponse> result = commentService.getFilteredComments(taskId, keyword, after, before, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("important content", result.getContent().get(0).content());
        verify(commentRepository).findFilteredComments(eq(taskId), eq(keyword),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void deleteComment_shouldDeleteIfAuthorMatches() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Some content");
        comment.setAuthorUsername("testuser"); // matches mocked SecurityContext

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowAccessDenied_whenUserIsNotAuthor() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthorUsername("otheruser"); // not the authenticated user

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void updateComment_shouldUpdateIfAuthorMatches() {
        // Arrange
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("Updated content", 100L);

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setContent("Old content");
        existingComment.setAuthorUsername("testuser"); // matches mocked SecurityContext

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CommentResponse response = commentService.updateComment(commentId, request);

        // Assert
        assertEquals("Updated content", response.content());
        verify(commentRepository).save(existingComment);
    }

    @Test
    void updateComment_shouldThrowException_whenCommentNotFound() {
        // Arrange
        Long commentId = 99L;
        CommentRequest request = new CommentRequest("Attempted update", 100L);
        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(commentId, request));
        verify(commentRepository, never()).save(any());
    }
}