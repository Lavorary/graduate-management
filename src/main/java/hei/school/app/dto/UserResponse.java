package hei.school.app.dto;

import java.util.UUID;

import hei.school.app.security.model.UserRole;

public record UserResponse(UUID id, String email, String firstName, String lastName, UserRole role) {

}
