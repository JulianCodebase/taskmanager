package de.personal.taskservice.service.impl;

import de.personal.common.messaging.TaskEventType;
import de.personal.taskservice.annotation.AuditLog;
import de.personal.taskservice.messaging.TaskEventProducer;
import de.personal.taskservice.model.Task;
import de.personal.taskservice.repository.TaskRepository;
import de.personal.taskservice.service.TaskCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCleanupServiceImpl implements TaskCleanupService {

    private final TaskRepository taskRepository;
    private final TaskEventProducer taskEventProducer;

    @Override
    @AuditLog(desc = "Scheduled Cleanup of Old Deleted Tasks")
    @Scheduled(cron = "0 0 2 * * *") // Happens every day at 2 am
    public int cleanupOldDeletedTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<Task> tasksToDelete = taskRepository.findByDeletedTrueAndDeletedAtBefore(threshold);
        // Deleting tasks automatically deletes their associated comments
        // because of cascade = ALL and orphanRemoval = true
        taskRepository.deleteAll(tasksToDelete);

        // Publishes deletion event for each task
        tasksToDelete.forEach(task -> taskEventProducer.sendTaskStatusEvent(task.getId(), TaskEventType.DELETED));

        return tasksToDelete.size();
    }
}
