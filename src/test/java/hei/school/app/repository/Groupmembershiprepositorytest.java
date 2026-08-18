package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JGroup;
import hei.school.app.repository.model.JGroupMembership;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GroupMembershipRepositoryTest extends FacadeIT {

  @Autowired private GroupMembershipRepository groupMembershipRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;

  private JUser saveStudent() {
    return userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jean")
            .lastName("R.")
            .role(UserRole.STUDENT)
            .email("jean+" + UUID.randomUUID() + "@hei.school")
            .password("x")
            .build());
  }

  @Test
  void should_find_all_memberships_ordered_by_start_date() {
    JUser student = saveStudent();
    JGroup groupA = groupRepository.save(JGroup.builder().ref("GROUPE-A").build());
    JGroup groupB = groupRepository.save(JGroup.builder().ref("GROUPE-B").build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(groupA)
            .startDate(LocalDate.of(2023, 9, 1))
            .endDate(LocalDate.of(2024, 1, 1))
            .build());
    groupMembershipRepository.save(
        JGroupMembership.builder().student(student).group(groupB).startDate(LocalDate.of(2024, 1, 1)).build());

    assertThat(groupMembershipRepository.findByStudentIdOrderByStartDateAsc(student.getId())).hasSize(2);
  }

  @Test
  void should_find_active_membership_when_end_date_is_null() {
    JUser student = saveStudent();
    JGroup group = groupRepository.save(JGroup.builder().ref("GROUPE-A").build());

    groupMembershipRepository.save(
        JGroupMembership.builder().student(student).group(group).startDate(LocalDate.of(2023, 9, 1)).build());

    assertThat(groupMembershipRepository.findByStudentIdAndEndDateIsNull(student.getId()));
  }
}
