package de.personal.taskmanager.controller;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        TaskRequest taskRequest = createSampleTaskRequest("Integration test task", "Test POST", LocalDate.now());
        Task task = TaskMapper.toTaskEntity(taskRequest);
        task.setId(1L);

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(TaskMapper.toTaskResponse(task));

        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void getAllTasks_shouldReturnEmptyList() throws Exception {
        Page<TaskResponse> emptyPage = Page.empty();

        when(taskService.getAllTasks(any(Boolean.class), any())).thenReturn(emptyPage);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        verify(taskService).getAllTasks(any(Boolean.class), any());
    }

    @Test
    void getAllTasks_shouldReturnAllTasks_whenDoneNotSpecified() throws Exception {
        TaskResponse task1 = createSampleTaskResponse(1L, "Task1", "task1 Desc", false);
        TaskResponse task2 = createSampleTaskResponse(2L, "Task2", "task2 Desc", false);

        List<TaskResponse> taskList = new ArrayList<>(List.of(task1, task2));
        Page<TaskResponse> taskPage = new PageImpl<>(taskList);

        when(taskService.getAllTasks(eq(null), any())).thenReturn(taskPage);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[1].id").value(2));

        verify(taskService).getAllTasks(eq(null), any());
    }

    @Test
    void getAllTasks_shouldReturnPartialTasks_whenDoneSpecified() throws Exception {
        TaskResponse task1 = createSampleTaskResponse(1L, "Task1", "task1 Desc", false);
        TaskResponse task2 = createSampleTaskResponse(2L, "Task2", "task2 Desc", false);
        TaskResponse task3 = createSampleTaskResponse(3L, "Task2", "task2 Desc", true);

        List<TaskResponse> taskList = List.of(task1, task2);
        Page<TaskResponse> taskPage = new PageImpl<>(taskList);

        when(taskService.getAllTasks(any(Boolean.class), any())).thenReturn(taskPage);

        mockMvc.perform(get("/tasks?done=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
        verify(taskService).getAllTasks(any(Boolean.class), any());
    }

    @Test
    void getSingleTask_shouldReturnTaskResponseWhenFound() throws Exception {
        TaskResponse taskResponse = createSampleTaskResponse(1L, "Task1", "task1 Desc", false);
        when(taskService.findTaskByIdOrThrow(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("task1 Desc"));
        verify(taskService).findTaskByIdOrThrow(1L);
    }

    @Test
    void getSingleTask_shouldReturn404WhenNotFound() throws Exception {
        when(taskService.findTaskByIdOrThrow(1L)).thenThrow(new TaskNotFoundException(1L));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_shouldReturnOKAndTaskResponse() throws Exception {
        TaskRequest request = createSampleTaskRequest("new task", "new task Desc", LocalDate.now());
        TaskResponse updated = createSampleTaskResponse(1L, "new task", "new task Desc", false);
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new task"));
    }

    @Test
    void updateTask_shouldReturnNotFound() throws Exception {
        TaskRequest request = createSampleTaskRequest("new task", "new task Desc", LocalDate.now());

        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsDone_shouldReturnOKAndTaskResponse() throws Exception {
        TaskResponse task = createSampleTaskResponse(1L, "Task", "task Desc", true);
        when(taskService.markTaskAsDone(1L)).thenReturn(task);

        mockMvc.perform(patch("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void markAsDone_shouldReturnNotFound() throws Exception {
        when(taskService.markTaskAsDone(1L)).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(patch("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    private TaskResponse createSampleTaskResponse(Long id, String title, String description, boolean done) {
        TaskResponse task = new TaskResponse();
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