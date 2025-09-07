package de.personal.notificationservice.messaging;

import de.personal.common.messaging.TaskEventType;
import de.personal.common.messaging.TaskStatusEvent;
import de.personal.notificationservice.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskEventConsumer {
    private final NotificationServiceImpl notificationServiceImpl;

    /**
     * Listens to task-events topic and logs messages
     */
    @KafkaListener(topics = "${kafka-task-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TaskStatusEvent event) {
        if (Objects.requireNonNull(event.eventType()) == TaskEventType.DONE) {
            notificationServiceImpl.notifyTaskCompleted(event);
        }
    }
}
