package de.personal.taskmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.respository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll(); //clean state
    }

    // Should return 201 CREATED and persist the task to the DB
    @Test
    void creatTask_shouldPersistAndReturn201() throws Exception {
        TaskRequest request = createSampleTaskRequest("task", "desc");

        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("task"));

        assertEquals(1, taskRepository.count()); // actually saved in DB
    }

    // Should return 200 OK with a list of all saved tasks
    @Test
    void getAllTasks_shouldReturn200AndAListOfTasks() throws Exception{
        Task task1 = createSampleTask("Task1", "Task1 Desc", LocalDate.now());
        Task task2 = createSampleTask("Task2", "Task2 Desc", LocalDate.now().plusDays(1));
        Task task3 = createSampleTask("Task3", "Task3 Desc", LocalDate.now().plusDays(2));
        taskRepository.saveAll(List.of(task1, task2, task3));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));

        assertEquals(3, taskRepository.count());
    }

    // Should return 200 OK with a list of completed tasks
    @Test
    void getAllTasks_shouldReturn200AndCompletedTasks() throws Exception{
        Task task1 = createSampleTask("Task1", "Task1 Desc", LocalDate.now());
        task1.setDone(true);
        Task task2 = createSampleTask("Task2", "Task2 Desc", LocalDate.now().plusDays(1));
        task2.setDone(true);
        Task task3 = createSampleTask("Task3", "Task3 Desc", LocalDate.now().plusDays(2));
        taskRepository.saveAll(List.of(task1, task2, task3));

        mockMvc.perform(get("/tasks?done=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andDo(print());

        assertEquals(3, taskRepository.count());
    }

    // Should return 200 OK with the task when it exists
    @Test
    void getSingleTask_shouldReturn200AndTask_whenFound() throws Exception {
        Task task = createSampleTask("Task", "Task Desc", LocalDate.now());
        task = taskRepository.save(task);

        mockMvc.perform(get("/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Task"))
                .andExpect(jsonPath("$.done").value(false));
    }

    // Should return 404 NOT FOUND when task doesn't exist
    @Test
    void getSingleTask_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    // Should return 200 OK with the updated task when found
    @Test
    void updateTask_shouldReturn200AndUpdatedTask_whenFound() throws Exception {
        Task task = createSampleTask("old task", "old desc");
        task = taskRepository.save(task);

        TaskRequest request = createSampleTaskRequest("updated task", "updated desc");

        mockMvc.perform(put("/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("updated task"))
                .andExpect(jsonPath("$.description").value("updated desc"));
    }

    // Should return 400 BAD_REQUEST when title is blank (validation failure)
    @Test
    void updateTask_shouldReturn400_whenNoTitle() throws Exception {
        TaskRequest request = createSampleTaskRequest("", "updated desc"); // title is blank

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print()); // prints full request and response
    }

    // Should return 200 OK with task marked as done
    @Test
    void markTaskAsDone_shouldReturn200AndTaskSetDone() throws Exception {
        Task task = createSampleTask("task", "desc");
        task = taskRepository.save(task);

        mockMvc.perform(patch("/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.done").value(true));
    }

    // Should return 204 No Content and remove the task from DB
    @Test
    void deleteTask_shouldReturn204() throws Exception {
        Task task = createSampleTask("task", "desc");
        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/" + task.getId()))
                .andExpect(status().isNoContent());
        assertEquals(0, taskRepository.count());
    }

    @Test
    void shouldPublishEven_whenMarkTaskAsDone() throws Exception {
        Task task = taskRepository.save(createSampleTask("task", "desc"));

        mockMvc.perform(patch("/tasks/" + task.getId()))
                .andExpect(status().isOk());

        Thread.sleep(2000); // simulate async wait.
    }

    private Task createSampleTask(String title, String description) {
        return createSampleTask(title, description, LocalDate.now());
    }

    private Task createSampleTask(String title, String description, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setDone(false);
        return task;
    }

    private static TaskRequest createSampleTaskRequest(String title, String description) {
        TaskRequest request = new TaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setDueDate(LocalDate.now());
        return request;
    }
}
