package de.personal.commentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 5, max = 500, message = "Comment must be between 5 and 500 characters.")
    private String content;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // use primitive here, because we don't need to represent a "null" state for deletion
    private boolean deleted;

    @Column(nullable = false)
    private Long taskId; // Reference to Task (not an entity relation)

    @Column(nullable = false)
    private String authorUsername; // Reference to a User

    // a JPA lifecycle callback,
    // triggered just before an entity is inserted into the database
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // JPA lifecycle callback,
    // running automatically before an entity update is flushed to the database.
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
