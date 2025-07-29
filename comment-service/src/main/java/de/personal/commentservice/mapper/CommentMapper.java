package de.personal.commentservice.mapper;

import de.personal.taskmanager.dto.task.TaskCommentRequest;
import de.personal.taskmanager.dto.task.TaskCommentResponse;
import de.personal.taskmanager.model.Task;
import de.personal.taskmanager.model.TaskComment;

public class CommentMapper {
    public static TaskComment toTaskComment(TaskCommentRequest commentRequest, Task task) {
        TaskComment taskComment = new TaskComment();
        taskComment.setTask(task);
        taskComment.setContent(commentRequest.getContent());

        return taskComment;
    }

    public static TaskCommentResponse toCommentResponse(TaskComment comment) {
        TaskCommentResponse commentResponse = new TaskCommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setAuthorUsername(comment.getAuthor().getUsername());
        commentResponse.setModifiedAt(comment.getModifiedAt());

        return commentResponse;
    }
}
