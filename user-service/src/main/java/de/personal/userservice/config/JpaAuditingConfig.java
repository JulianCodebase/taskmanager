package de.personal.userservice.config;

import de.personal.userservice.model.AppUser;
import de.personal.userservice.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    /**
     * Provides the current authenticated AppUser for JPA auditing purposes.
     * <p>
     * This AuditorAware bean is used by Spring Data JPA to automatically populate
     * auditing fields such as @CreatedBy and @LastModifiedBy based on the currently
     * logged-in user. If no user is authenticated or the authentication is anonymous,
     * no auditor is returned.
     *
     * @param userRepository the repository used to fetch the AppUser based on username
     * @return an Optional containing the current AppUser if available, otherwise empty
     */
    @Bean
    public AuditorAware<AppUser> userAuditorAware(UserRepository userRepository) {
        return () ->
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .filter(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                        .flatMap(auth -> userRepository.findByUsername(auth.getName()));
    }
}
