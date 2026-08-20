package hei.school.app.model;

import static hei.school.app.security.model.UserRole.STUDENT;
import static java.util.UUID.randomUUID;

public record AuthPayload(String firstName, String lastName, String email, String password) {
  public User toNewUser() {
    return User.builder()
        .id(randomUUID())
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .password(password)
        .role(STUDENT)
        .build();
  }
}
