package de.personal.taskmanager.service;

import de.personal.taskmanager.common.TaskMapper;
import de.personal.taskmanager.dto.task.TaskRequest;
import de.personal.taskmanager.dto.task.TaskResponse;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.model.TaskStatus;
import de.personal.taskmanager.respository.TaskRepository;
import de.personal.taskmanager.service.impl.TaskServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    TaskServiceImpl taskService;

    @NotNull
    private static TaskRequest createSampleTaskRequest(String title, String description) {
        return createSampleTaskRequest(title, description, LocalDate.now());
    }

    @NotNull
    private static TaskRequest createSampleTaskRequest(String title, String description, LocalDate dueDate) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle(title);
        taskRequest.setDescription(description);
        taskRequest.setDueDate(dueDate);
        return taskRequest;
    }

    @Test
    void createTask() {
        TaskRequest taskRequest = createSampleTaskRequest("task", "description");
        when(taskRepository.save(any(Task.class))).thenReturn(TaskMapper.toTaskEntity(taskRequest));

        TaskResponse result = taskService.createTask(taskRequest);

        assertEquals("task", result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_shouldThrowException_whenDuplicateTasksExist() {
        TaskRequest taskRequest = createSampleTaskRequest("task", "description");

        when(taskRepository.existsByTitleAndDueDate(taskRequest.getTitle(), taskRequest.getDueDate()))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskRequest));
    }

    @Test
    void updateTask() {
        TaskRequest taskRequest = createSampleTaskRequest("task", "description");

        TaskRequest newTaskRequest = createSampleTaskRequest("Updated task", "Updated description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(TaskMapper.toTaskEntity(taskRequest)));
        when(taskRepository.save(any(Task.class))).thenReturn(TaskMapper.toTaskEntity(newTaskRequest));

        TaskResponse updatedTask = taskService.updateTask(1L, newTaskRequest);

        assertEquals("Updated task", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findTaskById() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void markTaskAsDone() {
        TaskRequest taskRequest = createSampleTaskRequest("task", "description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(TaskMapper.toTaskEntity(taskRequest)));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TaskResponse completedTask = taskService.markTaskAsDone(1L);
        assertEquals(TaskStatus.DONE, completedTask.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }
}