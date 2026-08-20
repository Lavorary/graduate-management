package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.security.model.UserRole;
import org.junit.jupiter.api.Test;

class CreateUserDTOTest extends FacadeIT {

  @Test
  void should_build_create_user_dto_with_builder() {
    CreateUserDTO dto =
        CreateUserDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .email("john@hei.school")
            .password("password123")
            .build();

    assertThat(dto).isNotNull();
    assertThat(dto.firstName()).isEqualTo("John");
    assertThat(dto.lastName()).isEqualTo("Doe");
    assertThat(dto.role()).isEqualTo(UserRole.STUDENT);
    assertThat(dto.email()).isEqualTo("john@hei.school");
    assertThat(dto.password()).isEqualTo("password123");
  }

  @Test
  void should_create_create_user_dto_for_teacher() {
    CreateUserDTO dto =
        CreateUserDTO.builder()
            .firstName("Jane")
            .lastName("Smith")
            .role(UserRole.TEACHER)
            .email("jane@hei.school")
            .password("securePass")
            .build();

    assertThat(dto.role()).isEqualTo(UserRole.TEACHER);
    assertThat(dto.email()).isEqualTo("jane@hei.school");
  }

  @Test
  void should_create_create_user_dto_for_admin() {
    CreateUserDTO dto =
        CreateUserDTO.builder()
            .firstName("Admin")
            .lastName("User")
            .role(UserRole.ADMIN)
            .email("admin@hei.school")
            .password("adminPass")
            .build();

    assertThat(dto.role()).isEqualTo(UserRole.ADMIN);
  }
}
