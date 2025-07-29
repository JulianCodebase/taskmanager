package de.personal.userservice.service;

import de.personal.taskmanager.model.AppUser;

public interface UserStatsService {
    void handleTaskCompletion(AppUser user);
}
