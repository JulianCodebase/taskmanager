package de.personal.commentservice.messaging;

import de.personal.commentservice.service.CommentService;
import de.personal.common.messaging.TaskEventType;
import de.personal.common.messaging.TaskStatusEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskStatusEventConsumerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private TaskStatusEventConsumer consumer;

    @Test
    void shouldHandleSoftDeletedEvent() {
        Long taskId = 1L;
        consumer.consume(new TaskStatusEvent(taskId, TaskEventType.DELETED));
        verify(commentService).deleteComment(taskId);
    }
}