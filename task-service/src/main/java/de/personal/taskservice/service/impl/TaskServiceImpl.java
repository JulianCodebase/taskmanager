package de.personal.taskservice.service.impl;

import de.personal.taskservice.dto.TaskRequest;
import de.personal.taskservice.dto.TaskResponse;
import de.personal.taskservice.exception.TaskNotFoundException;
import de.personal.taskservice.mapper.TaskMapper;
import de.personal.taskservice.messaging.TaskCompletedEvent;
import de.personal.taskservice.messaging.TaskEventProducer;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.model.TaskStatus;
import de.personal.taskservice.repository.TaskRepository;
import de.personal.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    private final TaskEventProducer taskEventProducer;

    /**
     * Create a new task based on the provided request.
     * Prevents creating duplicate tasks with the same title and due date.
     *
     * @param taskRequest the request object containing task details
     * @return the created task as a response DTO
     * @throws IllegalArgumentException if a duplicate task already exists
     */
    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = TaskMapper.toTaskEntity(taskRequest);
        boolean exists = taskRepository.existsByTitleAndDueDate(task.getTitle(), task.getDueDate());
        if (exists) {
            throw new IllegalArgumentException("A task with this title and due date already exists");
        }

        Task savedTask = taskRepository.save(task);
        return TaskMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setPriority(taskRequest.getPriority());
        existingTask.setStatus(taskRequest.getStatus());

        Task savedTask = taskRepository.save(existingTask);

        return TaskMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse findTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .map(TaskMapper::toTaskResponse)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Retrieve all tasks that are not soft-deleted.
     * Only tasks with {@code deleted = false} are returned.
     *
     * @param pageable pagination and sorting information
     * @return a page of active (undeleted) tasks
     */
    @Override
    public Page<TaskResponse> getAllActiveTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAllByDeletedFalse(pageable);
        return page.map(TaskMapper::toTaskResponse);
    }

    /**
     * Soft delete a task by marking it as deleted.
     * Sets {@code deleted = true} and records the deletion time.
     *
     * @param id the ID of the task to soft-delete
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    /**
     * Restore a specific soft deleted task.
     * @param id the ID of the task to restore
     * @return the restored task
     */
    @Override
    public TaskResponse restoreTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (!task.isDeleted()) {
            throw new IllegalStateException("Task is not soft deleted");
        }

        task.setDeleted(false);
        task.setDeletedAt(null);
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toTaskResponse(savedTask);
    }

    @Override
    public int restoreAllSoftDeletedTasks() {
        return taskRepository.restoreSoftDeletedTasks();
    }

    /**
     * Mark a task as completed (status DONE).
     * Also publishes a task completion event.
     *
     * @param id       the ID of the task to mark as done
     * @param username
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public TaskResponse markTaskAsDone(Long id, String username) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );

        task.setStatus(TaskStatus.DONE);
        Task savedTask = taskRepository.save(task);

        TaskCompletedEvent event = new TaskCompletedEvent(task.getId(), username, task.getDescription());
        taskEventProducer.sendTaskCompletedEvent(task.getId(), username, task.getDescription());
        return TaskMapper.toTaskResponse(savedTask);
    }

    /**
     * Permanently delete a task immediately, bypassing soft delete.
     *
     * @param id the ID of the task to delete
     */
    @Override
    public void forceDeleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );

        taskRepository.delete(task); // deleting a task automatically deletes its comments because of cascade type
        taskEventProducer.sendTaskDeletedEvent(task.getId());
    }
}
