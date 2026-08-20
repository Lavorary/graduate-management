package hei.school.app.dto;

import java.util.UUID;

import hei.school.app.security.model.UserRole;

public record AuthResponse(String token, UUID userId, String email, UserRole role) {

}
