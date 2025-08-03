package de.personal.taskservice.service.impl;

import de.personal.taskservice.messaging.TaskEventProducer;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskCleanupServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskCleanupServiceImpl taskCleanupService;

    @Test
    void cleanupOldDeletedTasks() {
        // Arrange
        Task task1 = new Task(); task1.setId(1L);
        Task task2 = new Task(); task2.setId(2L);
        List<Task> oldTasks = List.of(task1, task2);

        when(taskRepository.findByDeletedTrueAndDeletedAtBefore(any())).thenReturn(oldTasks);

        // Act
        int deletedCount = taskCleanupService.cleanupOldDeletedTasks();

        // Assert
        verify(taskRepository).deleteAll(oldTasks);
        verify(taskEventProducer).sendTaskDeletedEvent(task1.getId());
        verify(taskEventProducer).sendTaskDeletedEvent(task2.getId());
        assertEquals(oldTasks.size(), deletedCount);
    }
}