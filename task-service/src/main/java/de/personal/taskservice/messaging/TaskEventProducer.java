package de.personal.taskservice.messaging;

import de.personal.common.messaging.TaskEventType;
import de.personal.common.messaging.TaskStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer that sends task completion events to a Kafka topic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    @Value("${kafka-task-topic}")
    private String TOPIC = "task-events";

    private final KafkaTemplate<String, TaskStatusEvent> kafkaTemplate;

    public void sendTaskStatusEvent(Long taskId, TaskEventType eventType) {
        TaskStatusEvent event = new TaskStatusEvent(taskId, eventType);
        log.info(">>> Sending task even to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
