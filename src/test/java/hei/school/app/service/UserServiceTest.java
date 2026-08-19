package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.CreateUserDTO;
import hei.school.app.DTOs.UserDTO;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private UserService userService;

  private JUser jUser(UUID id, String firstName, String lastName, UserRole role, String email) {
    return JUser.builder()
        .id(id)
        .firstName(firstName)
        .lastName(lastName)
        .role(role)
        .email(email)
        .password("encodedPassword")
        .build();
  }

  private User userModel(UUID id, String firstName, String lastName, UserRole role, String email) {
    return new User(id, firstName, lastName, role, email, "encodedPassword");
  }

  @Test
  void should_create_user() {
    UUID id = UUID.randomUUID();
    CreateUserDTO createDTO =
        new CreateUserDTO("John", "Doe", UserRole.STUDENT, "john@hei.school", "password123");

    JUser savedEntity = jUser(id, "John", "Doe", UserRole.STUDENT, "john@hei.school");
    User model = userModel(id, "John", "Doe", UserRole.STUDENT, "john@hei.school");

    when(userRepository.findByEmail("john@hei.school")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(JUser.class))).thenReturn(savedEntity);
    when(userMapper.toModel(savedEntity)).thenReturn(model);

    UserDTO result = userService.create(createDTO);

    ArgumentCaptor<JUser> captor = ArgumentCaptor.forClass(JUser.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getFirstName()).isEqualTo("John");
    assertThat(captor.getValue().getLastName()).isEqualTo("Doe");
    assertThat(captor.getValue().getRole()).isEqualTo(UserRole.STUDENT);
    assertThat(captor.getValue().getEmail()).isEqualTo("john@hei.school");
    assertThat(captor.getValue().getPassword()).isEqualTo("encodedPassword");

    assertThat(result.firstName()).isEqualTo("John");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.role()).isEqualTo(UserRole.STUDENT);
    assertThat(result.email()).isEqualTo("john@hei.school");
  }

  @Test
  void should_throw_when_email_already_exists_on_create() {
    CreateUserDTO createDTO =
        new CreateUserDTO("John", "Doe", UserRole.STUDENT, "john@hei.school", "password123");

    when(userRepository.findByEmail("john@hei.school"))
        .thenReturn(
            Optional.of(
                jUser(UUID.randomUUID(), "John", "Doe", UserRole.STUDENT, "john@hei.school")));

    assertThatThrownBy(() -> userService.create(createDTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("An account already exists for this email");
  }

  @Test
  void should_get_user_by_id() {
    UUID id = UUID.randomUUID();
    JUser entity = jUser(id, "John", "Doe", UserRole.STUDENT, "john@hei.school");
    User model = userModel(id, "John", "Doe", UserRole.STUDENT, "john@hei.school");

    when(userRepository.findById(id)).thenReturn(Optional.of(entity));
    when(userMapper.toModel(entity)).thenReturn(model);

    UserDTO result = userService.getById(id);
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.firstName()).isEqualTo("John");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.email()).isEqualTo("john@hei.school");
  }

  @Test
  void should_throw_when_user_not_found_by_id() {
    UUID id = UUID.randomUUID();
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getById(id))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(id.toString());
  }

  @Test
  void should_get_user_by_email() {
    UUID id = UUID.randomUUID();
    String email = "john@hei.school";
    JUser entity = jUser(id, "John", "Doe", UserRole.STUDENT, email);
    User model = userModel(id, "John", "Doe", UserRole.STUDENT, email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(entity));
    when(userMapper.toModel(entity)).thenReturn(model);

    UserDTO result = userService.getByEmail(email);
    assertThat(result.email()).isEqualTo(email);
    assertThat(result.firstName()).isEqualTo("John");
  }

  @Test
  void should_throw_when_user_not_found_by_email() {
    String email = "john@hei.school";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getByEmail(email))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(email);
  }

  @Test
  void should_find_by_role() {
    UUID id = UUID.randomUUID();
    UserRole role = UserRole.STUDENT;
    JUser entity = jUser(id, "John", "Doe", role, "john@hei.school");
    User model = userModel(id, "John", "Doe", role, "john@hei.school");

    when(userRepository.findByRole(role)).thenReturn(List.of(entity));
    when(userMapper.toModel(entity)).thenReturn(model);

    List<UserDTO> results = userService.findByRole(role);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().role()).isEqualTo(role);
  }

  @Test
  void should_return_empty_list_when_no_users_found_by_role() {
    UserRole role = UserRole.STUDENT;
    when(userRepository.findByRole(role)).thenReturn(List.of());

    assertThat(userService.findByRole(role)).isEmpty();
  }
}
