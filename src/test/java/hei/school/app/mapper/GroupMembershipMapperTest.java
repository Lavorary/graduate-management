package hei.school.app.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Group;
import hei.school.app.model.GroupMembership;
import hei.school.app.model.User;
import hei.school.app.repository.model.JGroup;
import hei.school.app.repository.model.JGroupMembership;
import hei.school.app.repository.model.JUser;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class GroupMembershipMapperTest {
  @Autowired private GroupMembershipMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JUser jUser = JUser.builder().id(UUID.randomUUID()).firstName("Alice").build();
    JGroup jGroup = JGroup.builder().id(UUID.randomUUID()).ref("G1").build();
    LocalDate start = LocalDate.of(2026, 1, 1);
    LocalDate end = LocalDate.of(2026, 6, 1);

    JGroupMembership jMembership =
        JGroupMembership.builder()
            .id(UUID.randomUUID())
            .student(jUser)
            .group(jGroup)
            .startDate(start)
            .endDate(end)
            .build();

    GroupMembership domain = mapper.toModel(jMembership);

    assertThat(domain.student().firstName()).isEqualTo("Alice");
    assertThat(domain.group().ref()).isEqualTo("G1");
    assertThat(domain.startDate()).isEqualTo(start);
    assertThat(domain.endDate()).isEqualTo(end);
  }

  @Test
  void shouldMapDomainToJpa() {
    User user = User.builder().id(UUID.randomUUID()).firstName("Bob").build();
    Group group = Group.builder().id(UUID.randomUUID()).ref("G2").build();
    LocalDate start = LocalDate.of(2026, 2, 1);

    GroupMembership domain =
        GroupMembership.builder()
            .id(UUID.randomUUID())
            .student(user)
            .group(group)
            .startDate(start)
            .endDate(null)
            .build();

    JGroupMembership jMembership = mapper.toEntity(domain);

    assertThat(jMembership.getStudent().getFirstName()).isEqualTo("Bob");
    assertThat(jMembership.getGroup().getRef()).isEqualTo("G2");
    assertThat(jMembership.getStartDate()).isEqualTo(start);
    assertThat(jMembership.getEndDate()).isNull();
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JGroupMembership> jList =
        List.of(
            JGroupMembership.builder().id(UUID.randomUUID()).build(),
            JGroupMembership.builder().id(UUID.randomUUID()).build());

    List<GroupMembership> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<GroupMembership> domains =
        List.of(
            GroupMembership.builder().id(UUID.randomUUID()).build(),
            GroupMembership.builder().id(UUID.randomUUID()).build());

    List<JGroupMembership> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
  }
}