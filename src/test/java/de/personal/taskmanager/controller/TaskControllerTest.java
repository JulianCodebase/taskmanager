package de.personal.taskmanager.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import de.personal.taskmanager.exception.TaskNotFoundException;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.service.TaskService;
import de.personal.taskmanager.util.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TaskService taskService;

    @Test
    void createTask_shouldReturn201AndTaskResponse() throws Exception {
        TaskRequest taskRequest = createSampleTaskRequest("Integration test task", "Test POST", LocalDate.of(2025, 1, 1));
        Task task = TaskMapper.toTaskEntity(taskRequest);
        task.setId(1L);

        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void getAllTasks_shouldReturnEmptyList() throws Exception {
        List<Task> tasks = new ArrayList<>();

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(taskService).getAllTasks();
    }

    @Test
    void getAllTasks_shouldReturnNonEmptyList() throws Exception {
        Task task1 = createSampleTask(1L, "Task1", "task1 Desc", false);
        Task task2 = createSampleTask(2L, "Task2", "task2 Desc", false);

        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(taskService).getAllTasks();
    }

    @Test
    void getSingleTask_shouldReturnTaskResponseWhenFound() throws Exception {
        Task task = createSampleTask(1L, "Task1", "task1 Desc", false);

        when(taskService.findTaskById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("task1 Desc"));
        verify(taskService).findTaskById(1L);
    }

    @Test
    void getSingleTask_shouldReturn404WhenNotFound() throws Exception {
        when(taskService.findTaskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_shouldReturnOKAndTaskResponse() throws Exception {
        TaskRequest request = createSampleTaskRequest("new task", "new task Desc", LocalDate.now());

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(TaskMapper.toTaskEntity(request));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new task"));
    }

    @Test
    void updateTask_shouldReturnNotFound() throws Exception {
        TaskRequest request = createSampleTaskRequest("new task", "new task Desc", LocalDate.now());

        when(taskService.updateTask(eq(1L), any(Task.class))).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsDone_shouldReturnOKAndTaskResponse() throws Exception {
        Task task = createSampleTask(1L, "Task", "task Desc", true);

        when(taskService.markTaskAsDone(1L)).thenReturn(task);

        mockMvc.perform(patch("/tasks/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void markAsDone_shouldReturnNotFound() throws Exception {
        when(taskService.markTaskAsDone(1L)).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(patch("/tasks/1/done"))
                .andExpect(status().isNotFound());
    }

    private Task createSampleTask(Long id, String title, String description, boolean done) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(LocalDate.now());
        task.setDone(done);
        return task;
    }

    private TaskRequest createSampleTaskRequest(String title, String description, LocalDate dueDate) {
        TaskRequest request = new TaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setDueDate(dueDate);
        return request;
    }
}