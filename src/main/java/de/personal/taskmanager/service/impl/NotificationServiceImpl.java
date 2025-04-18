package de.personal.taskmanager.service.impl;

import de.personal.taskmanager.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles notifications or side effects based on task-related events.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyTaskCompleted(String taskMessage) {
        log.info("Notifying user: {}", taskMessage);
    }
}
