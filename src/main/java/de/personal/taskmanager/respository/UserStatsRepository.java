package de.personal.taskmanager.respository;

import de.personal.taskmanager.model.AppUser;
import de.personal.taskmanager.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    Optional<UserStats> findByUser(AppUser user);
}
