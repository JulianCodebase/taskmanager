package de.personal.commentservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "task-service", url = "${task.service.url}")
public interface TaskClient {
    /**
     * Sends a request to task-service to verify that a task with the given ID exists.
     * <p>
     * If the task does not exist or is soft-deleted, task-service will return an error
     * (e.g. 404 Not Found), which should be handled by the caller.
     *
     *
     * @param id the ID of the task to validate
     */
    @GetMapping("/tasks/{id}")
    void ensureTaskExists(@PathVariable("id") Long id);
}
