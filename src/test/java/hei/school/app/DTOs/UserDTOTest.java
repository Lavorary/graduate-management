package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserDTOTest extends FacadeIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_create_user_dto_from_entity() {
        UUID id = UUID.randomUUID();
        JUser saved = userRepository.save(
            JUser.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.STUDENT)
                .email("john@hei.school")
                .password("encodedPassword")
                .build()
        );

        UserDTO dto = UserDTO.builder()
            .id(saved.getId())
            .firstName(saved.getFirstName())
            .lastName(saved.getLastName())
            .role(saved.getRole())
            .email(saved.getEmail())
            .build();

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.firstName()).isEqualTo("John");
        assertThat(dto.lastName()).isEqualTo("Doe");
        assertThat(dto.role()).isEqualTo(UserRole.STUDENT);
        assertThat(dto.email()).isEqualTo("john@hei.school");
    }

    @Test
    void should_build_user_dto_with_builder() {
        UUID id = UUID.randomUUID();
        UserDTO dto = UserDTO.builder()
            .id(id)
            .firstName("Jane")
            .lastName("Smith")
            .role(UserRole.TEACHER)
            .email("jane@hei.school")
            .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.firstName()).isEqualTo("Jane");
        assertThat(dto.lastName()).isEqualTo("Smith");
        assertThat(dto.role()).isEqualTo(UserRole.TEACHER);
        assertThat(dto.email()).isEqualTo("jane@hei.school");
    }

    @Test
    void should_create_user_dto_without_password() {
        UserDTO dto = UserDTO.builder()
            .id(UUID.randomUUID())
            .firstName("Bob")
            .lastName("Johnson")
            .role(UserRole.ADMIN)
            .email("bob@hei.school")
            .build();

        assertThat(dto).hasNoNullFieldsOrPropertiesExcept();
    }
}