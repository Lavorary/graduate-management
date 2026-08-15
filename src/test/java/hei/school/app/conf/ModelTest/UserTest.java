package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.enums.Roles;
import hei.school.app.model.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void should_expose_all_fields() {
    UUID id = UUID.randomUUID();
    User user = new User(id, "Jean", "Rakoto", Roles.STUDENT, "jean@hei.school", "hashed");

    assertThat(user.id()).isEqualTo(id);
    assertThat(user.firstName()).isEqualTo("Jean");
    assertThat(user.lastName()).isEqualTo("Rakoto");
    assertThat(user.role()).isEqualTo(Roles.STUDENT);
    assertThat(user.email()).isEqualTo("jean@hei.school");
    assertThat(user.password()).isEqualTo("hashed");
  }

  @Test
  void should_allow_null_last_name() {
    User user =
        new User(UUID.randomUUID(), "Jean", null, Roles.TEACHER, "jean@hei.school", "hashed");

    assertThat(user.lastName()).isNull();
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    User u1 = new User(id, "Jean", "R.", Roles.ADMIN, "j@hei.school", "x");
    User u2 = new User(id, "Jean", "R.", Roles.ADMIN, "j@hei.school", "x");

    assertThat(u1).isEqualTo(u2);
    assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
  }
}
