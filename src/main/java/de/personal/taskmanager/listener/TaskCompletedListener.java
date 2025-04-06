package de.personal.taskmanager.listener;

import de.personal.taskmanager.event.TaskCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskCompletedListener {

    @Async
    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        try {
            log.info("📬 Simulating sending email for task ID: {}", event.getTaskId());
            Thread.sleep(1500); // simulate delay
            log.info("✅ Email sent for task ID: {}", event.getTaskId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Failed to simulate email sending", e);
        }
    }
}
