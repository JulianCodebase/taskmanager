package de.personal.commentservice.repository;

import de.personal.taskmanager.model.TaskComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<TaskComment, Long> {
    @Query("""
            SELECT comment FROM TaskComment comment
            WHERE comment.task.id = :taskId
            AND (:keyword IS NULL OR LOWER(comment.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:after IS NULL OR comment.createdAt >= :after)
            AND (:before IS NULL OR comment.createdAt <= :before)
            """)
    Page<TaskComment> findFilteredComments(@Param("taskId") Long taskId,
                                           @Param("keyword") String keyword,
                                           @Param("after") LocalDateTime after,
                                           @Param("before") LocalDateTime before,
                                           Pageable pageable);
}

