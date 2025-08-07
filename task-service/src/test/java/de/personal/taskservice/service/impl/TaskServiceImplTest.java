package de.personal.taskservice.service.impl;

import de.personal.common.messaging.TaskEventType;
import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.exception.TaskNotFoundException;
import de.personal.taskservice.mapper.TaskMapper;
import de.personal.taskservice.messaging.TaskEventProducer;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.model.TaskPriority;
import de.personal.taskservice.model.TaskStatus;
import de.personal.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskRequest request;

    @BeforeEach
    void setup() {
        this.request = new TaskRequest(
                "task title",
                "description",
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS,
                LocalDate.now()
        );
    }

    @Test
    void createTask_shouldCreateAndReturnTaskResponse() {
        // Arrange
        Task task = TaskMapper.toTaskEntity(request);
        when(taskRepository.existsByTitleAndDueDate(task.getTitle(), task.getDueDate())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertEquals(response.title(), request.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findTask_shouldReturnTask_whenExists() {
        Long taskId = 1L;
        Task existingTask = TaskMapper.toTaskEntity(request);
        existingTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        TaskResponse response = taskService.findTaskByIdOrThrow(taskId);

        assertEquals(response.id(), existingTask.getId());
        assertEquals(response.title(), existingTask.getTitle());
        assertEquals(response.description(), existingTask.getDescription());
    }

    // Since we are testing the task-does-not-exist case here,
    // we don’t need to retest this in the later test cases
    @Test
    void findTask_shouldThrowException_whenNotExists() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskByIdOrThrow(taskId));
    }

    @Test
    void updateTask() {
        Long taskId = 1L;
        Task existingTask = TaskMapper.toTaskEntity(request);
        existingTask.setId(taskId);

        TaskRequest updatedRequest = new TaskRequest(
                "updated title",
                "updated description",
                TaskPriority.HIGH,
                TaskStatus.TODO,
                LocalDate.now().plusDays(1)
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).then(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(taskId, updatedRequest);

        assertEquals(updatedRequest.title(), response.title());
        assertEquals(updatedRequest.description(), response.description());
        assertEquals(updatedRequest.priority(), response.priority());
        assertEquals(updatedRequest.status(), response.status());
    }

    @Test
    void getAllActiveTasks() {
        Task task1 = new Task(); task1.setTitle("task 1");
        Task task2 = new Task(); task2.setTitle("task 2");
        List<Task> taskList = List.of(task1, task2);
        Page<Task> taskPage = new PageImpl<>(taskList);
        Pageable pageable = PageRequest.of(0, 10);

        when(taskRepository.findAllByDeletedFalse(pageable)).thenReturn(taskPage);

        Page<TaskResponse> response = taskService.getAllActiveTasks(pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals("task 1", response.getContent().get(0).title());
    }

    @Test
    void deleteTask() {
        Long taskId = 1L;
        Task task = TaskMapper.toTaskEntity(request);
        task.setId(taskId);
        task.setDeleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.softDeleteTask(taskId);

        assertTrue(task.isDeleted());
        assertNotNull(task.getDescription());

    }

    @Test
    void restoreTask_shouldRestoreTask_whenDeleted() {
        Long taskId = 1L;
        Task task = TaskMapper.toTaskEntity(request);
        task.setDeleted(true);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.restoreTask(taskId);

        assertEquals(taskId, response.id());
        assertFalse(task.isDeleted());
        assertNull(task.getDeletedAt());
    }

    @Test
    void restoreTask_shouldThrowException_whenNotDeleted() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setDeleted(false);

        // This is necessary, if not specified, it returns an empty Optional by default
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> taskService.restoreTask(taskId));
    }

    @Test
    void restoreAllSoftDeletedTasks() {
        int restoreCount = 5;
        when(taskRepository.restoreSoftDeletedTasks()).thenReturn(restoreCount);

        int result = taskService.restoreAllSoftDeletedTasks();

        assertEquals(restoreCount, result);
    }

    @Test
    void markTaskAsDone() {
        Long taskId = 1L;
        String username = "user";
        Task task = TaskMapper.toTaskEntity(request);
        task.setId(taskId);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.markTaskAsDone(taskId, username);

        assertEquals(taskId, response.id());
        assertEquals(TaskStatus.DONE, response.status());
        verify(taskEventProducer).sendTaskStatusEvent(taskId, TaskEventType.DONE);
    }

    @Test
    void forceDeleteTask() {
        Long taskId = 1L;
        Task task = TaskMapper.toTaskEntity(request);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository).delete(task);
        verify(taskEventProducer).sendTaskStatusEvent(taskId, TaskEventType.DELETED);
    }
}