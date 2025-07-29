package de.personal.userservice.service.impl;

import de.personal.taskmanager.dto.auth.AuthRegisterRequest;
import de.personal.taskmanager.dto.auth.AuthRequest;
import de.personal.taskmanager.dto.auth.AuthResponse;
import de.personal.taskmanager.model.AppUser;
import de.personal.taskmanager.respository.UserRepository;
import de.personal.taskmanager.security.JwtUtil;
import de.personal.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        String username = authRequest.getUsername();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new AuthResponse(username, null, "Invalid username or password");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication: " + username));
        return new AuthResponse(username, user.getUserRole(), "Login successful. Token: " + jwtUtil.generateToken(username));
    }

    @Override
    public AuthResponse register(AuthRegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new AuthResponse(registerRequest.getUsername(), null, "User already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserRole(registerRequest.getUserRole());
        userRepository.save(user);
        return new AuthResponse(user.getUsername(), user.getUserRole(), "User successfully created");
    }
}
