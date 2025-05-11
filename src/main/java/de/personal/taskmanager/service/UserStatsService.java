package de.personal.taskmanager.service;

import de.personal.taskmanager.model.AppUser;

public interface UserStatsService {
    void handleTaskCompletion(AppUser user);
}
