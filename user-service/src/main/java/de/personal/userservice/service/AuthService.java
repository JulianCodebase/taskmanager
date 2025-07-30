package de.personal.userservice.service;


import de.personal.userservice.dto.AuthRegisterRequest;
import de.personal.userservice.dto.AuthRequest;
import de.personal.userservice.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);

    AuthResponse register(AuthRegisterRequest registerRequest);
}
