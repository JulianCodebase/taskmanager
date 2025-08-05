package de.personal.commentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import de.personal.commentservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters for unit test
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentRequest request = new CommentRequest("New comment", 1L);
        CommentResponse response = new CommentResponse(1L, "New comment", "testuser", LocalDateTime.now(), LocalDateTime.now());

        when(commentService.addComment(any())).thenReturn(response);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("New comment"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getCommentsByTask_shouldReturnCommentPage() throws Exception {
        CommentResponse response = new CommentResponse(1L, "Filtered comment", "testuser", LocalDateTime.now(), LocalDateTime.now());
        Page<CommentResponse> page = new PageImpl<>(List.of(response));

        when(commentService.getFilteredComments(anyLong(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/comments/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Filtered comment"))
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(1L);
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        CommentRequest updateRequest = new CommentRequest("Updated comment", 1L);
        CommentResponse updatedResponse = new CommentResponse(1L, "Updated comment", "testuser", LocalDateTime.now(), LocalDateTime.now());

        when(commentService.updateComment(eq(1L), any())).thenReturn(updatedResponse);

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }
}