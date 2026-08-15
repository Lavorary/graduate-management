package hei.school.app.model;

import hei.school.app.enums.Roles;
import java.util.UUID;

public record User(
    UUID id, String firstName, String lastName, Roles role, String email, String password) {}
