package hei.school.app.security.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
  ADMIN,
  TEACHER,
  STUDENT;

  public String role() {
    return name();
  }

  @Override
  public String getAuthority() {
    return "ROLE_" + name();
  }

  @Override
  public String toString() {
    return role();
  }
}
