package de.personal.taskmanager.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.personal.taskmanager.model.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true) // Include parent information in `toString`
public class AuthRegisterRequest extends AuthRequest {
    @JsonProperty("role")
    private UserRole userRole; // USER or ADMIN
}
