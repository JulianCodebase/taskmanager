package de.personal.commentservice.messaging;

import de.personal.commentservice.service.CommentService;
import de.personal.common.messaging.TaskEventType;
import de.personal.common.messaging.TaskStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TaskStatusEventConsumer {

    private final CommentService commentService;

    @KafkaListener(topics = "${kafka-task-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TaskStatusEvent event) {
        if (Objects.requireNonNull(event.eventType()) == TaskEventType.DELETED) {
            commentService.deleteCommentsByTaskId(event.taskId());
        }
    }
}
