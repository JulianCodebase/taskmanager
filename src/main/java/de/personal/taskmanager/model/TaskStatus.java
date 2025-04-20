package de.personal.taskmanager.model;

/**
 * Defines the possible status values for a task in the system.
 * <p>
 * Each status represents a stage in the task's lifecycle, useful for tracking progress.
 * </p>
 *
 * <ul>
 *   <li><b>TODO</b> – Task is created but not yet started.</li>
 *   <li><b>IN_PROGRESS</b> – Task is currently being worked on.</li>
 *   <li><b>BLOCKED</b> – Task is temporarily halted due to an external dependency or issue.</li>
 *   <li><b>DONE</b> – Task has been completed.</li>
 * </ul>
 */
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    DONE
}
