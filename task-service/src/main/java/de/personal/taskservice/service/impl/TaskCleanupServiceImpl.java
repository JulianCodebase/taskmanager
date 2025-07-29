package de.personal.taskservice.service.impl;

import de.personal.taskmanager.annotation.AuditLog;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.respository.TaskRepository;
import de.personal.taskmanager.service.TaskCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCleanupServiceImpl implements TaskCleanupService {

    private final TaskRepository taskRepository;

    @Override
    @AuditLog(desc = "Scheduled Cleanup of Old Deleted Tasks")
    @Scheduled(cron = "0 0 2 * * *") // Happens every day at 2 am
    public int cleanupOldDeletedTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<Task> tasksToDelete = taskRepository.findByDeletedTrueAndDeletedAtBefore(threshold);
        // Deleting tasks automatically deletes their associated comments
        // because of cascade = ALL and orphanRemoval = true
        taskRepository.deleteAll(tasksToDelete);

        return tasksToDelete.size();
    }
}
