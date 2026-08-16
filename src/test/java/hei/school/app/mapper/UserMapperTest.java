package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.User;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class UserMapperTest {
  @Autowired private UserMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    UUID id = UUID.randomUUID();
    JUser jUser =
        JUser.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("john@doe.com")
            .role(UserRole.STUDENT)
            .password("hashed")
            .build();

    User domain = mapper.toModel(jUser);

    assertThat(domain.id()).isEqualTo(id);
    assertThat(domain.firstName()).isEqualTo("John");
    assertThat(domain.password()).isEqualTo("hashed");
  }

  @Test
  void shouldMapDomainToJpa() {
    UUID id = UUID.randomUUID();
    User domain =
        User.builder()
            .id(id)
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@smith.com")
            .role(UserRole.TEACHER)
            .password("newHash")
            .build();

    JUser jUser = mapper.toEntity(domain);

    assertThat(jUser.getId()).isEqualTo(id);
    assertThat(jUser.getFirstName()).isEqualTo("Jane");
    assertThat(jUser.getRole()).isEqualTo(UserRole.TEACHER);
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JUser> jUsers =
        List.of(
            JUser.builder().id(UUID.randomUUID()).firstName("A").build(),
            JUser.builder().id(UUID.randomUUID()).firstName("B").build());

    List<User> domains = mapper.toModel(jUsers);

    assertThat(domains).hasSize(2);
    assertThat(domains.get(0).firstName()).isEqualTo("A");
    assertThat(domains.get(1).firstName()).isEqualTo("B");
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<User> domains =
        List.of(
            User.builder().id(UUID.randomUUID()).firstName("A").build(),
            User.builder().id(UUID.randomUUID()).firstName("B").build());

    List<JUser> jUsers = mapper.toEntity(domains);

    assertThat(jUsers).hasSize(2);
    assertThat(jUsers.get(0).getFirstName()).isEqualTo("A");
    assertThat(jUsers.get(1).getFirstName()).isEqualTo("B");
  }
}
