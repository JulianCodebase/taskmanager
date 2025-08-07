package de.personal.commentservice.repository;

import de.personal.commentservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT comment FROM Comment comment
            WHERE comment.taskId = :taskId
            AND (:keyword IS NULL OR LOWER(comment.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:after IS NULL OR comment.createdAt >= :after)
            AND (:before IS NULL OR comment.createdAt <= :before)
            """)
    Page<Comment> findFilteredComments(@Param("taskId") Long taskId,
                                           @Param("keyword") String keyword,
                                           @Param("after") LocalDateTime after,
                                           @Param("before") LocalDateTime before,
                                           Pageable pageable);

    int deleteAllByTaskId(Long taskId);
}

