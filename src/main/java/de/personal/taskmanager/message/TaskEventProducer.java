package de.personal.taskmanager.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer that sends task completion events to a Kafka topic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "task-events";

    /**
     * Publishes a task completion message to the Kafka topic.
     */
    public void sendTaskCompletedMessage(String message) {
        log.info(">>> Sending task even to Kafka: {}", message);
        kafkaTemplate.send(TOPIC, message);
    }
}
