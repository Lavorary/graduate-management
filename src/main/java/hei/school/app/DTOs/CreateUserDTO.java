package hei.school.app.DTOs;

import hei.school.app.security.model.UserRole;
import lombok.Builder;


@Builder
public record CreateUserDTO(
    String firstName, String lastName, UserRole role, String email, String password) {}