package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.*;
import hei.school.app.security.model.UserRole;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GroupMembershipRepositoryTest extends FacadeIT {

  @Autowired private GroupMembershipRepository groupMembershipRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CursusRepository cursusRepository;
  @Autowired private CourseRepository courseRepository;

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
        JGroupMembership.builder()
            .student(student)
            .group(groupB)
            .startDate(LocalDate.of(2024, 1, 1))
            .build());

    assertThat(groupMembershipRepository.findByStudentIdOrderByStartDateAsc(student.getId()))
        .hasSize(2);
  }

  @Test
  void should_find_active_membership_when_end_date_is_null() {
    JUser student = saveStudent();
    JGroup group = groupRepository.save(JGroup.builder().ref("GROUPE-A").build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2023, 9, 1))
            .build());

    assertThat(groupMembershipRepository.findByStudentIdAndEndDateIsNull(student.getId()));
  }

   @Test
  void should_return_true_when_teacher_has_access_to_student() {
    JUser student = saveStudent();
    JUser teacher = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Teacher")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("teacher+" + UUID.randomUUID() + "@hei.school")
            .password("x")
            .build());

    JCursus cursus = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());

    JCourse course = courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    JGroup group = groupRepository.save(
        JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus)).build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2023, 9, 1))
            .build());

    boolean result = groupMembershipRepository.existsTeacherAccessToStudent(teacher.getId(), student.getId());

    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_when_teacher_does_not_have_access_to_student() {
    JUser student = saveStudent();
    JUser teacher = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Teacher")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("teacher+" + UUID.randomUUID() + "@hei.school")
            .password("x")
            .build());

    JCursus cursus = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());

    JGroup group = groupRepository.save(
        JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus)).build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2023, 9, 1))
            .build());

    boolean result = groupMembershipRepository.existsTeacherAccessToStudent(teacher.getId(), student.getId());

    assertThat(result).isFalse();
  }

    @Test
  void should_return_true_when_student_has_active_membership_for_cursus() {
    JUser student = saveStudent();

    JCursus cursus = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());

    JGroup group = groupRepository.save(
        JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus)).build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2023, 9, 1))
            .build());

    boolean result = groupMembershipRepository.existsByStudentIdAndCursusId(student.getId(), cursus.getId());

    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_when_student_has_no_membership_for_cursus() {
    JUser student = saveStudent();

    JCursus cursus = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());

    boolean result = groupMembershipRepository.existsByStudentIdAndCursusId(student.getId(), cursus.getId());

    assertThat(result).isFalse();
  }

  @Test
  void should_return_false_when_student_has_expired_membership_for_cursus() {
    JUser student = saveStudent();

    JCursus cursus = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());

    JGroup group = groupRepository.save(
        JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus)).build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2022, 9, 1))
            .endDate(LocalDate.of(2023, 1, 1))
            .build());

    boolean result = groupMembershipRepository.existsByStudentIdAndCursusId(student.getId(), cursus.getId());

    assertThat(result).isFalse();
  }

  @Test
  void should_find_cursus_ids_for_student() {
    JUser student = saveStudent();

    JCursus cursus1 = cursusRepository.save(
        JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCursus cursus2 = cursusRepository.save(
        JCursus.builder().name("MathLog").description("m").year("2026").build());

    JGroup group1 = groupRepository.save(
        JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus1)).build());
    JGroup group2 = groupRepository.save(
        JGroup.builder().ref("GROUPE-B").cursus(Set.of(cursus2)).build());

    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group1)
            .startDate(LocalDate.of(2023, 9, 1))
            .build());
    groupMembershipRepository.save(
        JGroupMembership.builder()
            .student(student)
            .group(group2)
            .startDate(LocalDate.of(2024, 1, 1))
            .build());

    List<UUID> result = groupMembershipRepository.findCursusIdsForStudent(student.getId());

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(cursus1.getId(), cursus2.getId());
  }
}
