package de.personal.commentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.model.Comment;
import de.personal.commentservice.repository.CommentRepository;
import de.personal.commentservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Test
    @WithMockUser(username = "testuser")
    void addComment_shouldReturnCreatedComment_interService() throws Exception {
        // Add a comment to that task
        Long taskId = 150L;
        CommentRequest request = new CommentRequest("Integration comment", taskId);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Integration comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_shouldReturnNotFound_whenTaskNotExists() throws Exception {
        Long taskId = 0L;

        CommentRequest request = new CommentRequest("Comment test", taskId);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCommentsByTask_shouldReturnFilteredComments() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Comment test");
        comment.setTaskId(1L);
        comment.setAuthorUsername("testuser");
        commentRepository.save(comment);

        mockMvc.perform(get("/comments/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Comment test"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteComment_shouldReturnNoContent() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Comment test");
        comment.setTaskId(1L);
        comment.setAuthorUsername("testuser");
        commentRepository.save(comment);

        mockMvc.perform(delete("/comments/" + comment.getId()))
                .andExpect(status().isNoContent());

        assertFalse(commentRepository.findById(comment.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateComment_shouldReturnUpdatedContent() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Old comment");
        comment.setTaskId(1L);
        comment.setAuthorUsername("testuser");
        commentRepository.save(comment);

        CommentRequest updateRequest = new CommentRequest("Updated", 1L);

        mockMvc.perform(put("/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated"));
    }

    @Test
    void deleteCommentsByTaskId_shouldDeleteAllCommentsForTask() {
        Long taskId = 1L;
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Comment comment = new Comment();
            comment.setTaskId(taskId);
            comment.setAuthorUsername("user");
            comment.setContent("content");
            comments.add(comment);
        }
        commentRepository.saveAll(comments);

        int deletedCommentsCount = commentService.deleteCommentsByTaskId(taskId);

        assertEquals(5, deletedCommentsCount);
    }
}
