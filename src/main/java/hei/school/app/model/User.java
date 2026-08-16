package hei.school.app.model;

import hei.school.app.security.model.UserRole;
import java.util.UUID;
import lombok.Builder;

@Builder
public record User(
    UUID id, String firstName, String lastName, UserRole role, String email, String password) {}
