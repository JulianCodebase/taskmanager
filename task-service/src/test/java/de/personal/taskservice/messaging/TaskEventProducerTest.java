package de.personal.taskservice.messaging;

import de.personal.common.messaging.TaskEventType;
import de.personal.common.messaging.TaskStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskEventProducerTest {

    @Mock
    private KafkaTemplate<String, TaskStatusEvent> kafkaTemplate;

    private TaskEventProducer producer;

    @BeforeEach
    void setup() {
        producer = new TaskEventProducer(kafkaTemplate);
    }

    @Test
    void sendTaskCompletedEvent_shouldSerializeAndSend() {
        // Arrange
        Long taskId = 1L;

        // Act
        producer.sendTaskStatusEvent(taskId, TaskEventType.DONE);

        // Assert
        ArgumentCaptor<TaskStatusEvent> captor = ArgumentCaptor.forClass(TaskStatusEvent.class);
        verify(kafkaTemplate).send(eq("task-events"), captor.capture());

        TaskStatusEvent event = captor.getValue();
        assertEquals(1L, event.taskId());
        assertEquals(TaskEventType.DONE, event.eventType());
    }
}