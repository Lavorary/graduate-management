package hei.school.app.model;

import java.util.UUID;

import hei.school.app.security.model.UserRole;
import lombok.Builder;

@Builder
public record User(UUID id, String firstName, String lastName, UserRole role, String email, String password) {

}
