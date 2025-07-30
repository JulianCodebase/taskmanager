package de.personal.userservice.service.impl;

import de.personal.userservice.model.AppUser;
import de.personal.userservice.model.UserStats;
import de.personal.userservice.repository.UserStatsRepository;
import de.personal.userservice.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    private final UserStatsRepository userStatsRepository;

    /**
     * Called whenever a task is marked as DONE.
     * Updates the user's XP and task streak accordingly.
     */
    @Override
    public void handleTaskCompletion(AppUser user) {
        UserStats stats = userStatsRepository.findByUser(user)
                .orElse(createNewUserStats(user));

        stats.incrementXpPoints();

        LocalDate today = LocalDate.now();
        if (stats.getLastTaskCompletedDate() == null) {
            stats.setCurrentStreak(1);
        } else if (today.minusDays(1).isEqual(stats.getLastTaskCompletedDate())) {
            stats.incrementStreak();
        } else if (!today.isEqual(stats.getLastTaskCompletedDate())) {
            stats.resetStreak();
        }

        stats.setLastTaskCompletedDate(today);

        userStatsRepository.save(stats);
    }

    private UserStats createNewUserStats(AppUser user) {
        UserStats userStats = new UserStats();
        userStats.setUser(user);
        return userStats;
    }
}
