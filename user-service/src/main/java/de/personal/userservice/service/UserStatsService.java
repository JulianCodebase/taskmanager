package de.personal.userservice.service;

import de.personal.userservice.model.AppUser;

public interface UserStatsService {
    void handleTaskCompletion(AppUser user);
}
