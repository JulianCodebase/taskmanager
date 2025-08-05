package de.personal.taskservice;

import de.personal.common.model.UserRole;
import de.personal.taskservice.config.TestTokenGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class JwtFilterIntegrationTest {

    @Autowired
    private TestTokenGenerator tokenGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessProtectedEndpoint_withValidJwt_shouldSucceed() throws Exception {
        // Given: a valid token
        String token = tokenGenerator.generateToken("testuser", UserRole.ROLE_USER); // assuming this method exists

        // When & Then: perform authenticated request
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()); // or isForbidden / other, depending on endpoint rules
    }

    @Test
    void accessProtectedEndpoint_withoutJwt_shouldFail() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withInvalidJwt_shouldFail() throws Exception {
        String invalidToken = "Bearer.invalid.token";

        mockMvc.perform(get("/tasks")
                        .header("Authorization", invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
