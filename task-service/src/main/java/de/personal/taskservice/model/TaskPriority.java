package de.personal.taskservice.model;

/**
 * Represents the priority level of a task.
 * <p>
 * This enum is used to classify tasks based on their urgency and importance,
 * which can affect task ordering, notifications, or deadline handling.
 * </p>
 *
 * <p>Available priorities:</p>
 * <ul>
 *   <li><b>LOW</b> – Minor urgency or impact; can be addressed later.</li>
 *   <li><b>MEDIUM</b> – Normal priority; should be handled in a timely manner.</li>
 *   <li><b>HIGH</b> – Critical importance; requires immediate attention.</li>
 * </ul>
 */
public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
