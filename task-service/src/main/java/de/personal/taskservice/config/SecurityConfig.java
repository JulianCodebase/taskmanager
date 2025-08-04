package de.personal.taskservice.config;

import de.personal.common.exception.JwtExceptionHandler;
import de.personal.common.security.JwtFilter;
import de.personal.common.util.JwtUtil;
import de.personal.common.util.SecurityErrorWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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
    public JwtExceptionHandler jwtExceptionHandler(SecurityErrorWriter securityErrorWriter) {
        return new JwtExceptionHandler(securityErrorWriter);
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil, JwtExceptionHandler jwtExceptionHandler) {
        return new JwtFilter(jwtUtil, jwtExceptionHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(configure ->
                        configure.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
