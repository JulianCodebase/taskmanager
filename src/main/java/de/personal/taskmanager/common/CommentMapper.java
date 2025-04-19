package de.personal.taskmanager.common;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import de.personal.taskmanager.model.AppUser;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.model.TaskComment;

public class CommentMapper {
    public static TaskComment toTaskComment(TaskCommentRequest commentRequest, Task task, AppUser author) {
        TaskComment taskComment = new TaskComment();
        taskComment.setTask(task);
        taskComment.setContent(commentRequest.getContent());
        taskComment.setAuthor(author);

        return taskComment;
    }

    public static TaskCommentResponse toCommentResponse(TaskComment comment) {
        TaskCommentResponse commentResponse = new TaskCommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setAuthorUsername(comment.getAuthor().getUsername());

        return commentResponse;
    }
}
