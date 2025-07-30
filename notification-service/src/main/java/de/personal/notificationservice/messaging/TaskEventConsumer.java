package de.personal.notificationservice.messaging;

import de.personal.notificationservice.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskEventConsumer {
    private final NotificationServiceImpl notificationServiceImpl;

    /**
     * Listens to task-events topic and logs messages
     */
    @KafkaListener(topics = "task-events", groupId = "task-manager-group")
    public void consume(ConsumerRecord<String, String> record) {
        String message = record.value();
        log.info(">>> Received task event from Kafka: {}", message);

        // Delegate to notification logic
        notificationServiceImpl.notifyTaskCompleted(message);
    }
}
