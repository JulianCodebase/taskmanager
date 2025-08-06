package de.personal.common.messaging;

public record TaskStatusEvent(
        Long taskId,
        TaskEventType eventType
) {
}
