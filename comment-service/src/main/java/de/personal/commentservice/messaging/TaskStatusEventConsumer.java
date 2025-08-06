package de.personal.commentservice.messaging;

import de.personal.commentservice.service.CommentService;
import de.personal.common.messaging.TaskStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskStatusEventConsumer {

    private final CommentService commentService;

    @KafkaListener(topics = "${kafka-task-topic}", groupId = "comment-group")
    public void consume(TaskStatusEvent event) {
        
    }
}
