package hei.school.app.DTOs;

import hei.school.app.security.model.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDTO(
        UUID id, String firstName, String lastName, UserRole role, String email) {}
