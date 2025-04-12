package de.personal.taskmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    // The value must start with "ROLE_" to comply with Spring Security conventions (e.g., "ROLE_USER", "ROLE_ADMIN").
    private String role;
}
