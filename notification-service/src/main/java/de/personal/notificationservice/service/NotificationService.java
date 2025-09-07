package de.personal.notificationservice.service;

import de.personal.common.messaging.TaskStatusEvent;

public interface NotificationService {
    void notifyTaskCompleted(TaskStatusEvent taskStatusEvent);
}
