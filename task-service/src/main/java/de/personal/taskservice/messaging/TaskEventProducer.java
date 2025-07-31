package de.personal.taskservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    /**
     * Publishes a task completion event to the Kafka topic.
     */
    public void sendTaskCompletedEvent(Long taskId, String username, String taskDescription) {
        sendEventToKafka(new TaskCompletedEvent(taskId, username, taskDescription));
    }

    public void sendTaskDeletedEvent(Long taskId) {
        sendEventToKafka(new TaskDeletedEvent(taskId));
    }

    private void sendEventToKafka(TaskEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            log.info(">>> Sending task even to Kafka: {}", jsonMessage);
            kafkaTemplate.send(TOPIC, jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
