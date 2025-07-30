package de.personal.userservice.service.impl;

import de.personal.userservice.dto.AuthRegisterRequest;
import de.personal.userservice.dto.AuthRequest;
import de.personal.userservice.dto.AuthResponse;
import de.personal.userservice.model.AppUser;
import de.personal.userservice.repository.UserRepository;
import de.personal.userservice.security.JwtUtil;
import de.personal.userservice.service.AuthService;
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
        String username = authRequest.username();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            authRequest.password()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication: " + username));
        return new AuthResponse(username, user.getUserRole(), "Login successful. Token: " + jwtUtil.generateToken(username));
    }

    @Override
    public AuthResponse register(AuthRegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            return new AuthResponse(registerRequest.username(), null, "User already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setUserRole(registerRequest.userRole());
        userRepository.save(user);
        return new AuthResponse(user.getUsername(), user.getUserRole(), "User successfully created");
    }
}
