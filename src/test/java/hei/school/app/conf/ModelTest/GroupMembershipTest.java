package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.enums.Roles;
import hei.school.app.model.Group;
import hei.school.app.model.GroupMembership;
import hei.school.app.model.User;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GroupMembershipTest {

  private final User user =
      new User(UUID.randomUUID(), "Jean", "R.", Roles.STUDENT, "j@hei.school", "x");
  private final Group group = new Group(UUID.randomUUID(), "GROUPE-A", null);

  @Test
  void should_expose_all_fields() {
    Timestamp start = Timestamp.valueOf("2023-09-01 00:00:00");
    Timestamp end = Timestamp.valueOf("2024-01-15 00:00:00");
    GroupMembership membership = new GroupMembership(UUID.randomUUID(), start, end, user, group);

    assertThat(membership.startDate()).isEqualTo(start);
    assertThat(membership.endDate()).isEqualTo(end);
    assertThat(membership.user()).isEqualTo(user);
    assertThat(membership.group()).isEqualTo(group);
  }

  @Test
  void should_allow_null_end_date_for_active_membership() {
    GroupMembership membership =
        new GroupMembership(
            UUID.randomUUID(), Timestamp.valueOf("2023-09-01 00:00:00"), null, user, group);

    assertThat(membership.endDate()).isNull();
  }
}
