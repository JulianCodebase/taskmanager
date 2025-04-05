package de.personal.taskmanager.service;

import de.personal.taskmanager.dto.TaskRequest;
import de.personal.taskmanager.dto.TaskResponse;
import de.personal.taskmanager.exception.TaskNotFoundException;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.respository.TaskRepository;
import de.personal.taskmanager.util.TaskMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // create a new task, and prevent duplicate titles for tasks on the same due date
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
        existingTask.setDone(taskRequest.isDone());

        Task savedTask = taskRepository.save(existingTask);

        return TaskMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse findTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .map(TaskMapper::toTaskResponse)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Page<TaskResponse> getAllTasks(Boolean done, Pageable pageable) {
        Page<Task> page = (done != null) ? taskRepository.findByDone(true, pageable)
                : taskRepository.findAll(pageable);
        return page.map(TaskMapper::toTaskResponse);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponse markTaskAsDone(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );

        task.setDone(true);
        return TaskMapper.toTaskResponse(taskRepository.save(task));
    }
}
