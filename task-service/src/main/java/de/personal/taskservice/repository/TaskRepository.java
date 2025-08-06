package de.personal.taskservice.repository;

import de.personal.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByTitleAndDueDate(String title, LocalDate dueDate);

    Page<Task> findAllByDeletedFalse(Pageable pageable);

    List<Task> findAllByDeletedTrue();

    /**
     * Find tasks that have been soft-deleted and whose deletedAt timestamp
     * is older than the given threshold.
     * @param threshold the cutoff datetime; task deleted before this time will be retrieved
     * @return a list of tasks matching the criteria
     */
    @Query("SELECT t FROM Task t WHERE t.deleted = true AND t.deletedAt <= :threshold")
    List<Task> findByDeletedTrueAndDeletedAtBefore(@Param("threshold") LocalDateTime threshold);

    Optional<Task> findByIdAndDeletedFalse(Long id);
}
