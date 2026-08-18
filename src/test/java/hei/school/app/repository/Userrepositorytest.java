package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserRepositoryTest extends FacadeIT {

  @Autowired private UserRepository userRepository;

  @Test
  void should_find_by_email() {
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jean")
            .lastName("R.")
            .role(UserRole.STUDENT)
            .email("jean@hei.school")
            .password("x")
            .build());

    assertThat(userRepository.findByEmail("jean@hei.school")).isPresent();
    assertThat(userRepository.findByEmail("inconnu@hei.school")).isEmpty();
  }

  @Test
  void should_find_by_role() {
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Marie")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("marie@hei.school")
            .password("x")
            .build());
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jean")
            .lastName("R.")
            .role(UserRole.STUDENT)
            .email("jean@hei.school")
            .password("x")
            .build());

    assertThat(userRepository.findByRole(UserRole.TEACHER)).extracting(JUser::getEmail).containsExactly("marie@hei.school");
  }
}
