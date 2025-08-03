package de.personal.taskservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import de.personal.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional // rolls back DB changes after each test
class TaskServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @WithMockUser(username = "user")
    void restoreTask() throws Exception {
        // Arrange: create and soft-delete a task
        Task task = new Task();
        task.setTitle("title");
        task.setDescription("description");
        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        task.setPriority(TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(patch("/tasks/{id}/restore", task.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("title"));
    }

}
