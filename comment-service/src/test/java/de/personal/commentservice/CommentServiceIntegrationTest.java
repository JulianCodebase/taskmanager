package de.personal.commentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.model.Comment;
import de.personal.commentservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "app-key=1i20hBJJioBo===ebw01920hONeoibno1=nOblfnfjk31wnoknjno")
class CommentServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @WithMockUser(username = "testuser")
    void addComment_shouldCreateComment() throws Exception {
        CommentRequest request = new CommentRequest("Comment content", 1L);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Comment content"))
                .andExpect(jsonPath("$.authorUsername").value("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCommentsByTask_shouldReturnFilteredComments() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Comment test");
        comment.setTaskId(1L);
        comment.setAuthorUsername("testuser");
        commentRepository.save(comment);

        mockMvc.perform(get("/api/comments/task/1"))
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

        mockMvc.perform(delete("/api/comments/" + comment.getId()))
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

        mockMvc.perform(put("/api/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated"));
    }
}
