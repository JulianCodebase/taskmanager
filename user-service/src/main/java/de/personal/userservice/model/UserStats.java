package de.personal.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the gamification statistics for a user in the TaskManager application.
 * <p>
 * This entity tracks:
 * <ul>
 *     <li>Total XP points earned by the user through task completions</li>
 *     <li>Current consecutive day streak of task completions</li>
 *     <li>The date of the last completed task</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserStats {
    // XP increment per task completion
    public static final int DEFAULT_XP_INCREMENT = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private int xPoints; // primitive ensures it has default value, free from null issues,

    private int currentStreak;

    private LocalDate lastTaskCompletedDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    /**
     * Increases the user's XP points by the default amount defined by {@code DEFAULT_XP_INCREMENT}.
     */
    public void incrementXpPoints() {
        this.xPoints += DEFAULT_XP_INCREMENT;
    }

    public void incrementStreak() {
        this.currentStreak++;
    }

    public void resetStreak() {
        this.currentStreak = 1;
    }
}
