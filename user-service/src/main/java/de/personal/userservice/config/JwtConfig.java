package de.personal.userservice.config;

import de.personal.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${app-key}")
    private String jwtSigningKey;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSigningKey);
    }
}
