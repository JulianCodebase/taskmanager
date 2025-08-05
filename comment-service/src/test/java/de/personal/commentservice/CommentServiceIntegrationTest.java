package de.personal.commentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.commentservice.config.TestTokenGenerator;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.TaskRequest;
import de.personal.commentservice.dto.TaskResponse;
import de.personal.commentservice.model.Comment;
import de.personal.commentservice.repository.CommentRepository;
import de.personal.common.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private TestTokenGenerator tokenGenerator;

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        String jwt = tokenGenerator.generateToken("testuser", UserRole.ROLE_USER);
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_shouldReturnCreatedComment_interService() throws Exception {
        // Create a task to make sure it exists
        TaskRequest taskRequest = new TaskRequest(
                "Sample Task",
                "desc",
                "MEDIUM",
                "TODO",
                LocalDate.now().plusDays(3));

        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(taskRequest),
                headers
        );

        ResponseEntity<TaskResponse> response = restTemplate.postForEntity(
                "http://localhost:8080/tasks/create",
                entity,
                TaskResponse.class);

        TaskResponse taskResponse = response.getBody();
        assertNotNull(taskResponse);

        // Add a comment to that task
        Long taskId = taskResponse.id();
        CommentRequest request = new CommentRequest("Integration comment", taskId);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Integration comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_shouldThrowException_whenTaskNotExists() throws Exception{
        Long taskId = 0L;

        CommentRequest request = new CommentRequest("Comment test", taskId);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Comment test"));
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
}
