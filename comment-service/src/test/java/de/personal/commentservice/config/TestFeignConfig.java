package de.personal.commentservice.config;

import de.personal.common.model.UserRole;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Feign client used during integration testing.
 *
 * <p>This config provides a {@link RequestInterceptor} that injects a mock
 * JWT token into the Authorization header of all outgoing Feign requests,
 * simulating authenticated inter-service communication.</p>
 *
 * <p>Used in test environments to ensure that services requiring authentication
 * (e.g., task-service) can be called without setting up real authentication flows.</p>
 */
@Configuration
@RequiredArgsConstructor
public class TestFeignConfig {

    private final TestTokenGenerator tokenGenerator;

    @Bean
    public RequestInterceptor testAuthInterceptor() {
        return requestTemplate -> {
            String mockToken = "Bearer " + tokenGenerator.generateToken("testuser", UserRole.ROLE_USER);
            requestTemplate.header("Authorization", mockToken);
        };
    }
}
