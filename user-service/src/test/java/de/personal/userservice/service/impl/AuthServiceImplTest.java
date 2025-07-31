package de.personal.userservice.service.impl;

import de.personal.userservice.dto.AuthRegisterRequest;
import de.personal.userservice.dto.AuthRequest;
import de.personal.userservice.dto.AuthResponse;
import de.personal.userservice.model.AppUser;
import de.personal.userservice.model.UserRole;
import de.personal.userservice.repository.UserRepository;
import de.personal.userservice.security.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_shouldReturnAuthResponseWithValidCredentials() {
        AuthRequest request = new AuthRequest("user", "123");
        AppUser user = new AppUser();
        user.setUsername("user");
        user.setUserRole(UserRole.ROLE_USER);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtGenerator.generateToken("user")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("user", response.username());
        assertEquals(UserRole.ROLE_USER, response.role());
        System.out.println(response);
    }

    @Test
    void login_shouldThrowExceptionWithInvalidCredentials() {
        AuthRequest request = new AuthRequest("user", "111");

        doThrow(new BadCredentialsException("Invalid")).when(authenticationManager)
                .authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void register_shouldCreateUser_whenUsernameNotExists() {
        AuthRegisterRequest request = new AuthRegisterRequest("user", "123", UserRole.ROLE_USER);
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hashed");
        when(userRepository.save(any())).then(i -> i.getArgument(0));

        AuthResponse response = authService.register(request);
        System.out.println(response);
        assertEquals("user", response.username());
        assertEquals(UserRole.ROLE_USER, response.role());
        assertTrue(response.message().contains("successfully"));
    }

    @Test
    void register_shouldTellUserExists_whenUsernameExists() {
        AuthRegisterRequest request = new AuthRegisterRequest("user", "123", UserRole.ROLE_USER);

        when(userRepository.existsByUsername("user")).thenReturn(true);

        AuthResponse response = authService.register(request);

        assertEquals("user", response.username());
        assertNull(response.role());
        assertTrue(response.message().contains("already exists"));
    }
}