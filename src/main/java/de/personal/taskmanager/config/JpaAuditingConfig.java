package de.personal.taskmanager.config;

import de.personal.taskmanager.model.AppUser;
import de.personal.taskmanager.respository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<AppUser> userAuditorAware(UserRepository userRepository) {
        return () ->
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .filter(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                        .flatMap(auth -> userRepository.findByUsername(auth.getName()));
    }
}
