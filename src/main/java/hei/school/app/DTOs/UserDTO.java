package hei.school.app.DTOs;

import hei.school.app.security.model.UserRole;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDTO(UUID id, String firstName, String lastName, UserRole role, String email) {}
