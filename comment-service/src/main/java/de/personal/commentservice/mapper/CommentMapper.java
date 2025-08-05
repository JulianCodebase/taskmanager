package de.personal.commentservice.mapper;


import de.personal.commentservice.dto.CommentRequest;
import de.personal.commentservice.dto.CommentResponse;
import de.personal.commentservice.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setTaskId(commentRequest.taskId());
        comment.setContent(commentRequest.content());

        return comment;
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
