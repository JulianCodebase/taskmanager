package de.personal.userservice.service;

import de.personal.taskmanager.dto.auth.AuthRegisterRequest;
import de.personal.taskmanager.dto.auth.AuthRequest;
import de.personal.taskmanager.dto.auth.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);

    AuthResponse register(AuthRegisterRequest registerRequest);
}
