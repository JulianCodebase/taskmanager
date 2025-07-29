package de.personal.taskservice.exception;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(Long id) {
        super("Task not found with ID: " + id);
    }
}
