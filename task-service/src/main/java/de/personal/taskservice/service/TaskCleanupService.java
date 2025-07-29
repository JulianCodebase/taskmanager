package de.personal.taskservice.service;

/**
 * Service for scheduled background cleanup tasks.
 */
public interface TaskCleanupService {
    /**
     * Delete tasks that were soft-deleted more than a defined retention period ago.
     *
     * @return the number of deleted tasks
     */
    int cleanupOldDeletedTasks();
}
