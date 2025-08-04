package de.personal.userservice.config;

import de.personal.common.exception.JwtExceptionHandler;
import de.personal.common.security.CustomAuthenticationEntryPoint;
import de.personal.common.security.JwtFilter;
import de.personal.common.util.JwtUtil;
import de.personal.common.util.SecurityErrorWriter;
import de.personal.userservice.exception.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app-key}")
    private String jwtSigningKey;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSigningKey);
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil, JwtExceptionHandler jwtExceptionHandler) {
        return new JwtFilter(jwtUtil, jwtExceptionHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtFilter jwtFilter,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler,
                                                   CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity in stateless JWT
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Protected endpoints
                );

        // Only apply JWT filter to endpoints that are not explicitly permitted
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Hash user passwords securely
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(SecurityErrorWriter securityErrorWriter) {
        return new CustomAuthenticationEntryPoint(securityErrorWriter);
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler(SecurityErrorWriter securityErrorWriter) {
        return new CustomAccessDeniedHandler(securityErrorWriter);
    }

    @Bean
    public JwtExceptionHandler jwtExceptionHandler(SecurityErrorWriter securityErrorWriter) {
        return new JwtExceptionHandler(securityErrorWriter);
    }
}
