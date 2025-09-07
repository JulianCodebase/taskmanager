package de.personal.notificationservice.service.impl;

import de.personal.common.messaging.TaskStatusEvent;
import de.personal.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles notifications or side effects based on task-related events.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyTaskCompleted(TaskStatusEvent taskStatusEvent) {
        log.info("Task {} is DONE", taskStatusEvent.taskId());
    }
}
