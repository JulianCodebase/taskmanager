package de.personal.taskservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private TaskEventProducer producer;

    @BeforeEach
    void setup() {
        producer = new TaskEventProducer(kafkaTemplate, new ObjectMapper());
    }

    @Test
    void sendTaskCompletedEvent_shouldSerializeAndSend() {
        // Arrange
        Long taskId = 1L;
        String username = "user";
        String description = "description";

        // Act
        producer.sendTaskCompletedEvent(taskId, username, description);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("task-events"), captor.capture());

        String message = captor.getValue();
        assertTrue(message.contains("\"taskId\":1"));
        assertTrue(message.contains("\"username\":\"user\""));
        assertTrue(message.contains("\"description\":\"description\""));
    }
}