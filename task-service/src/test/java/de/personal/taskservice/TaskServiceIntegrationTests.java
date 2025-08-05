package de.personal.taskservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import de.personal.taskservice.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user")
@Transactional // rolls back DB changes after each test
@ActiveProfiles("test")
class TaskServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task();
        task.setTitle("title");
        task.setDescription("description");
        task.setPriority(TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void createTask() throws Exception {
        String title = "task";
        LocalDate dueDate = LocalDate.now().plusDays(5);
        TaskRequest request = new TaskRequest(title, "desc", TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS, dueDate);

        mockMvc.perform(post("/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title));

        assertTrue(taskRepository.existsByTitleAndDueDate(title, dueDate));
    }

    @Test
    void getAllTasks() throws Exception {
        int listSize = 3;
        for (int i = 1; i <= listSize; i++) {
            Task eachTask = new Task();
            eachTask.setTitle("task" + i);
            eachTask.setDescription("desc" + i);
            eachTask.setDueDate(LocalDate.now().plusDays(1));
            taskRepository.save(eachTask);
        }

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5)) // page size
                .andExpect(jsonPath("$.totalElements").value(listSize))
                .andExpect(jsonPath("$.content[0].title").value("task1"));
    }

    @Test
    void getSingleTask_shouldThrowException_whenTaskNotExist() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: 9999"));
    }

    @Test
    void updateTask() throws Exception {
        task = taskRepository.save(task);

        TaskRequest updateRequest = new TaskRequest(
                "Updated title", "Updated desc",
                TaskPriority.HIGH, TaskStatus.TODO,
                LocalDate.now().plusDays(10)
        );

        mockMvc.perform(put("/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals("Updated title", updated.getTitle());
    }

    @Test
    void markTaskAsDone() throws Exception {
        task = taskRepository.save(task);

        mockMvc.perform(patch("/tasks/{id}", task.getId()))
                .andExpect(jsonPath("$.status").value("DONE"));

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    void deleteTask() throws Exception{
        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        // Active query should not find soft-deleted task
        Optional<Task> activeTask = taskRepository.findByIdAndDeletedFalse(task.getId());
        assertTrue(activeTask.isEmpty());

        // But still exists in the database
        Optional<Task> rawTask = taskRepository.findById(task.getId());
        assertTrue(rawTask.isPresent());
        assertTrue(rawTask.get().isDeleted());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void restoreAllSoftDeletedTasks_shouldFail_withValidRole() throws Exception{
        mockMvc.perform(patch("/tasks/restore"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void restoreAllSoftDeletedTasks() throws Exception{
        Task task1 = new Task();
        task1.setTitle("title1");
        task1.setDeleted(false);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("title2");
        task2.setDeleted(true);
        task2.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setTitle("title3");
        task3.setDeleted(true);
        task3.setDeletedAt(LocalDateTime.now());
        task3 = taskRepository.save(task3);

        mockMvc.perform(patch("/tasks/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restoredCount").value(2));

        // Clears the persistence context to avoid returning stale cached entities
        // after bulk DB updates
        entityManager.clear();

        Task restoredTask = taskRepository.findById(task3.getId()).orElseThrow();
        assertFalse(restoredTask.isDeleted());
        assertNull(restoredTask.getDeletedAt());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void forceDeleteTask() throws Exception{
        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}/force", task.getId()))
                .andExpect(status().isNoContent());

        assertTrue(taskRepository.findById(task.getId()).isEmpty());
    }

    @Test
    void restoreTask() throws Exception {
        // Arrange: create and soft-delete a task
        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        task = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(patch("/tasks/{id}/restore", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("title"));

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertFalse(updated.isDeleted());
    }

}
