package de.personal.commentservice.mapper;


import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import de.personal.commentservice.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentRequest commentRequest, Task task) {
        Comment Comment = new Comment();
        Comment.setTask(task);
        Comment.setContent(commentRequest.getContent());

        return Comment;
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setAuthorUsername(comment.getAuthor().getUsername());
        commentResponse.setModifiedAt(comment.getModifiedAt());

        return commentResponse;
    }
}
