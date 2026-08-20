package hei.school.app.dto;

import hei.school.app.security.model.UserRole;
import java.util.UUID;

public record UserResponse(
    UUID id, String email, String firstName, String lastName, UserRole role) {}
