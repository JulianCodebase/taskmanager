package de.personal.taskservice.messaging;

public record TaskDeletedEvent(Long taskId) implements TaskEvent {
}
