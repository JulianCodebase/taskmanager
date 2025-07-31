package de.personal.taskservice.messaging;

/**
 * Represents a domain event emitted when a task is marked as completed.
 * This message is sent by the task-service to Kafka, and consumed by other services
 * such as notification-service or user-service.
 *
 * @param taskId      the ID of the completed task
 * @param username    the username of the user who completed the task
 * @param description a brief description of the completed task
 */
public record TaskCompletedEvent (
        Long taskId,
        String username,
        String description
) implements TaskEvent {
}
