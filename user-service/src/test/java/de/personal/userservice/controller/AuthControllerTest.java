package de.personal.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.common.model.UserRole;
import de.personal.userservice.dto.AuthRegisterRequest;
import de.personal.userservice.dto.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// Use a temporary key for JWT during tests
@TestPropertySource(properties = "app-key=1i20hBJJioBo===ebw01920hONeoibno1=nOblfnfjk31wnoknjno")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRegisterRequest registerRequest;
    private AuthRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new AuthRegisterRequest("user", "123", UserRole.ROLE_USER);
        loginRequest = new AuthRequest("user", "123");
    }

    @Test
    void register_shouldCreateNewUser() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void register_shouldFailWithInvalidInput() throws Exception {
        AuthRegisterRequest invalidRequest = new AuthRegisterRequest(null, "", UserRole.ROLE_USER);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        // register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    void login_shouldFailWithInvalidCredentials() throws Exception {
        // register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthRequest wrongPassword = new AuthRequest("user", "1111");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPassword)))
                .andExpect(status().isUnauthorized());
    }
}