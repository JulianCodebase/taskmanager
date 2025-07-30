package de.personal.userservice.repository;

import de.personal.userservice.model.AppUser;
import de.personal.userservice.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    Optional<UserStats> findByUser(AppUser user);
}
