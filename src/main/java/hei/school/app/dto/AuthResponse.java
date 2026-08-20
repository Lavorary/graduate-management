package hei.school.app.dto;

import hei.school.app.security.model.UserRole;
import java.util.UUID;

public record AuthResponse(String token, UUID userId, String email, UserRole role) {}
