package de.personal.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.taskservice.config.CommonBeanConfig;
import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import de.personal.taskservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(CommonBeanConfig.class)
@WithMockUser("user") // Simulates an authenticated user with username "user" for all test methods
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskRequest request;
    private TaskResponse response;

    @BeforeEach
    void setup() {
        request = new TaskRequest("task", "desc", TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(7));
        response = new TaskResponse(1L, "task", "desc", TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(7));
    }

    @Test
    void createTask() throws Exception {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        // Adds a mock CSRF token to the request, required for non-GET methods when Spring Security is enabled
        mockMvc.perform(post("/tasks/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("task"));
    }

    @Test
    void getAllTasks() throws Exception {
        TaskResponse response1 = new TaskResponse(1L, "task 1", "desc 1", TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(7));
        TaskResponse response2 = new TaskResponse(2L, "task 2", "desc 2", TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(7));

        Page<TaskResponse> page = new PageImpl<>(List.of(response1, response2));

        when(taskService.getAllActiveTasks(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("task 1"))
                .andExpect(jsonPath("$.content[1].title").value("task 2"));
    }

    @Test
    void getSingleTask() throws Exception {
        when(taskService.findTaskByIdOrThrow(1L)).thenReturn(response);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("task"));
    }

    @Test
    void updateTask() throws Exception {
        Long taskId = 1L;

        when(taskService.updateTask(eq(taskId), any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value(response.title()));
    }

    @Test
    void markTaskAsDone() throws Exception {
        Long taskId = 1L;
        String username = "user";

        when(taskService.markTaskAsDone(taskId, username)).thenReturn(response);

        mockMvc.perform(patch("/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));
    }

    @Test
    void deleteTask() throws Exception {
        Long taskId = 1L;

        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).softDeleteTask(taskId);
    }

    @Test
    void restoreTask() throws Exception {
        Long taskId = 1L;

        when(taskService.restoreTask(taskId)).thenReturn(response);

        mockMvc.perform(patch("/tasks/{id}/restore", taskId)
                        .with(csrf()))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void restoreAllSoftDeletedTasks() throws Exception {
        int restoredCount = 3;

        when(taskService.restoreAllSoftDeletedTasks()).thenReturn(restoredCount);

        mockMvc.perform(patch("/tasks/restore")
                        .with(csrf()))
                .andExpect(jsonPath("$.restoredCount").value(restoredCount));
    }

    @Test
    void forceDeleteTask() throws Exception {
        Long taskId = 1L;

        mockMvc.perform(delete("/tasks/{id}/force", taskId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }
}