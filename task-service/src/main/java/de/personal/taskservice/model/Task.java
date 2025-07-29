package de.personal.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@ToString
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    /**
     * The list of comments associated with this task, which represents a one-to-many bidirectional
     * relationship where each TaskComment references back to its Task.
     * <p>
     * This relationship uses the following important configurations:
     * <ul>
     *     <li><b>mappedBy = "task"</b>: Specifies that the TaskComment entity owns the relationship through its 'task' field.</li>
     *     <li><b>cascade = CascadeType.ALL</b>: All persistence operations (persist, merge, remove, refresh) performed on Task will cascade to its comments.</li>
     *     <li><b>orphanRemoval = true</b>: If a comment is removed from the comments list, it will also be automatically deleted from the database.</li>
     * </ul>
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskComment> comments;
}
