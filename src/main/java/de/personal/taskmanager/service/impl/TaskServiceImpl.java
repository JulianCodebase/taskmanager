package de.personal.taskmanager.service.impl;

import de.personal.taskmanager.common.TaskMapper;
import de.personal.taskmanager.dto.task.TaskRequest;
import de.personal.taskmanager.dto.task.TaskResponse;
import de.personal.taskmanager.exception.TaskNotFoundException;
import de.personal.taskmanager.message.TaskEventProducer;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.model.TaskStatus;
import de.personal.taskmanager.respository.TaskRepository;
import de.personal.taskmanager.service.TaskService;
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

    /**
     * Update an existing task with new details.
     *
     * @param id the ID of the task to update
     * @param taskRequest the updated task information
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
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
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
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
     * Mark a task as completed (status DONE).
     * Also publishes a task completion event.
     *
     * @param id the ID of the task to mark as done
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public TaskResponse markTaskAsDone(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );

        task.setStatus(TaskStatus.DONE);
        Task savedTask = taskRepository.save(task);
        String message = String.format("Task completed: ID=%d, description=%s", task.getId(), task.getDescription());
        taskEventProducer.sendTaskCompletedMessage(message); // publish an event when the task is updated to database
        return TaskMapper.toTaskResponse(savedTask);
    }
}
