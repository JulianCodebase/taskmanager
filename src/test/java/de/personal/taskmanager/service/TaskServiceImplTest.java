package de.personal.taskmanager.service;

import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.respository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskServiceImpl taskService;

    @Test
    void createTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old title");
        task.setDescription("Old Desc");
        task.setDueDate(LocalDate.now());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(task);

        assertEquals("Old title", result.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old title");
        task.setDescription("Old Desc");
        task.setDueDate(LocalDate.now());

        Task newTask = new Task();
        newTask.setTitle("Updated title");
        newTask.setDescription("Updated description");
        newTask.setDone(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(1L, newTask);

        assertEquals("Updated title", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertTrue(updatedTask.getDone());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
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
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old title");
        task.setDescription("Old Desc");
        task.setDueDate(LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Task completedTask = taskService.markTaskAsDone(1L);
        assertTrue(completedTask.getDone());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }
}