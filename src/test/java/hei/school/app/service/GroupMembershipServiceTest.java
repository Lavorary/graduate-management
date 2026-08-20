package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.GroupMembershipDTO;
import hei.school.app.mapper.GroupMembershipMapper;
import hei.school.app.model.Group;
import hei.school.app.model.GroupMembership;
import hei.school.app.model.User;
import hei.school.app.repository.GroupMembershipRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JGroup;
import hei.school.app.repository.model.JGroupMembership;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupMembershipServiceTest {

  @Mock private GroupMembershipRepository groupMembershipRepository;
  @Mock private UserRepository userRepository;
  @Mock private GroupRepository groupRepository;
  @Mock private GroupMembershipMapper groupMembershipMapper;
  @InjectMocks private GroupMembershipService groupMembershipService;

  private JUser jStudent(UUID id) {
    return JUser.builder()
        .id(id)
        .firstName("Student")
        .lastName("One")
        .role(UserRole.STUDENT)
        .email("student@hei.school")
        .password("encoded")
        .build();
  }

  private JGroup jGroup(UUID id) {
    return JGroup.builder().id(id).ref("G1").cursus(Set.of()).build();
  }

  private User userModel(UUID id) {
    return new User(id, "Student", "One", UserRole.STUDENT, "student@hei.school", "encoded");
  }

  private Group groupModel(UUID id) {
    return new Group(id, "G1", Set.of());
  }

  @Test
  void should_create_membership() {
    UUID membershipId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 12, 31);

    JUser jStudent = jStudent(studentId);
    JGroup jGroup = jGroup(groupId);

    JGroupMembership savedEntity =
        JGroupMembership.builder()
            .id(membershipId)
            .startDate(startDate)
            .endDate(endDate)
            .student(jStudent)
            .group(jGroup)
            .build();

    User student = userModel(studentId);
    Group group = groupModel(groupId);
    GroupMembership model = new GroupMembership(membershipId, startDate, endDate, student, group);

    when(userRepository.findById(studentId)).thenReturn(Optional.of(jStudent));
    when(groupRepository.findById(groupId)).thenReturn(Optional.of(jGroup));
    when(groupMembershipRepository.save(any(JGroupMembership.class))).thenReturn(savedEntity);
    when(groupMembershipMapper.toModel(savedEntity)).thenReturn(model);

    GroupMembershipDTO result =
        groupMembershipService.create(startDate, endDate, studentId, groupId);

    ArgumentCaptor<JGroupMembership> captor = ArgumentCaptor.forClass(JGroupMembership.class);
    verify(groupMembershipRepository).save(captor.capture());
    assertThat(captor.getValue().getStartDate()).isEqualTo(startDate);
    assertThat(captor.getValue().getEndDate()).isEqualTo(endDate);
    assertThat(captor.getValue().getStudent()).isEqualTo(jStudent);
    assertThat(captor.getValue().getGroup()).isEqualTo(jGroup);

    assertThat(result.startDate()).isEqualTo(startDate);
    assertThat(result.endDate()).isEqualTo(endDate);
    assertThat(result.studentId()).isEqualTo(studentId);
    assertThat(result.groupId()).isEqualTo(groupId);
  }

  @Test
  void should_throw_when_student_not_found_on_create() {
    UUID studentId = UUID.randomUUID();
    when(userRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                groupMembershipService.create(LocalDate.now(), null, studentId, UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(studentId.toString());
  }

  @Test
  void should_throw_when_group_not_found_on_create() {
    UUID studentId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();

    when(userRepository.findById(studentId)).thenReturn(Optional.of(jStudent(studentId)));
    when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> groupMembershipService.create(LocalDate.now(), null, studentId, groupId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(groupId.toString());
  }

  @Test
  void should_get_membership_by_id() {
    UUID membershipId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 12, 31);

    JUser jStudent = jStudent(studentId);
    JGroup jGroup = jGroup(groupId);
    JGroupMembership entity =
        JGroupMembership.builder()
            .id(membershipId)
            .startDate(startDate)
            .endDate(endDate)
            .student(jStudent)
            .group(jGroup)
            .build();

    User student = userModel(studentId);
    Group group = groupModel(groupId);
    GroupMembership model = new GroupMembership(membershipId, startDate, endDate, student, group);

    when(groupMembershipRepository.findById(membershipId)).thenReturn(Optional.of(entity));
    when(groupMembershipMapper.toModel(entity)).thenReturn(model);

    GroupMembershipDTO result = groupMembershipService.getById(membershipId);
    assertThat(result.id()).isEqualTo(membershipId);
    assertThat(result.studentId()).isEqualTo(studentId);
    assertThat(result.groupId()).isEqualTo(groupId);
  }

  @Test
  void should_throw_when_membership_not_found_by_id() {
    UUID membershipId = UUID.randomUUID();
    when(groupMembershipRepository.findById(membershipId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> groupMembershipService.getById(membershipId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(membershipId.toString());
  }

  @Test
  void should_find_by_student() {
    UUID studentId = UUID.randomUUID();
    UUID membershipId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2024, 1, 1);

    JUser jStudent = jStudent(studentId);
    JGroup jGroup = jGroup(groupId);
    JGroupMembership entity =
        JGroupMembership.builder()
            .id(membershipId)
            .startDate(startDate)
            .student(jStudent)
            .group(jGroup)
            .build();

    User student = userModel(studentId);
    Group group = groupModel(groupId);
    GroupMembership model = new GroupMembership(membershipId, startDate, null, student, group);

    when(groupMembershipRepository.findByStudentIdOrderByStartDateAsc(studentId))
        .thenReturn(List.of(entity));
    when(groupMembershipMapper.toModel(entity)).thenReturn(model);

    List<GroupMembershipDTO> results = groupMembershipService.findByStudent(studentId);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).studentId()).isEqualTo(studentId);
    assertThat(results.get(0).groupId()).isEqualTo(groupId);
  }

  @Test
  void should_return_empty_list_when_no_memberships_found_by_student() {
    UUID studentId = UUID.randomUUID();
    when(groupMembershipRepository.findByStudentIdOrderByStartDateAsc(studentId))
        .thenReturn(List.of());

    assertThat(groupMembershipService.findByStudent(studentId)).isEmpty();
  }

  @Test
  void should_find_active_by_student() {
    UUID studentId = UUID.randomUUID();
    UUID membershipId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2024, 1, 1);

    JUser jStudent = jStudent(studentId);
    JGroup jGroup = jGroup(groupId);
    JGroupMembership entity =
        JGroupMembership.builder()
            .id(membershipId)
            .startDate(startDate)
            .student(jStudent)
            .group(jGroup)
            .endDate(null)
            .build();

    User student = userModel(studentId);
    Group group = groupModel(groupId);
    GroupMembership model = new GroupMembership(membershipId, startDate, null, student, group);

    when(groupMembershipRepository.findByStudentIdAndEndDateIsNull(studentId))
        .thenReturn(List.of(entity));
    when(groupMembershipMapper.toModel(entity)).thenReturn(model);

    Optional<GroupMembership> results = groupMembershipService.findActiveByStudent(studentId);
    assertThat(results.orElseThrow(null).student().id()).isEqualTo(studentId);
    assertThat(results.orElseThrow(null).group().id()).isEqualTo(groupId);
  }

  @Test
  void should_return_empty_list_when_no_active_memberships_found() {
    UUID studentId = UUID.randomUUID();
    when(groupMembershipRepository.findByStudentIdAndEndDateIsNull(studentId))
        .thenReturn(List.of());

    assertThat(groupMembershipService.findActiveByStudent(studentId)).isEmpty();
  }

  @Test
  void should_find_by_group() {
    UUID groupId = UUID.randomUUID();
    UUID membershipId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2024, 1, 1);

    JUser jStudent = jStudent(studentId);
    JGroup jGroup = jGroup(groupId);
    JGroupMembership entity =
        JGroupMembership.builder()
            .id(membershipId)
            .startDate(startDate)
            .student(jStudent)
            .group(jGroup)
            .build();

    User student = userModel(studentId);
    Group group = groupModel(groupId);
    GroupMembership model = new GroupMembership(membershipId, startDate, null, student, group);

    when(groupMembershipRepository.findByGroupId(groupId)).thenReturn(List.of(entity));
    when(groupMembershipMapper.toModel(entity)).thenReturn(model);

    List<GroupMembershipDTO> results = groupMembershipService.findByGroup(groupId);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).groupId()).isEqualTo(groupId);
  }

  @Test
  void should_return_empty_list_when_no_memberships_found_by_group() {
    UUID groupId = UUID.randomUUID();
    when(groupMembershipRepository.findByGroupId(groupId)).thenReturn(List.of());

    assertThat(groupMembershipService.findByGroup(groupId)).isEmpty();
  }

  @Test
  void should_return_true_when_teacher_has_access_to_student() {
    UUID teacherId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    when(groupMembershipRepository.existsTeacherAccessToStudent(teacherId, studentId))
        .thenReturn(true);

    assertThat(groupMembershipService.hasTeacherAccessToStudent(teacherId, studentId)).isTrue();
  }

  @Test
  void should_return_false_when_teacher_has_no_access_to_student() {
    UUID teacherId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    when(groupMembershipRepository.existsTeacherAccessToStudent(teacherId, studentId))
        .thenReturn(false);

    assertThat(groupMembershipService.hasTeacherAccessToStudent(teacherId, studentId)).isFalse();
  }

  @Test
  void should_return_true_when_student_is_in_cursus() {
    UUID studentId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    when(groupMembershipRepository.existsByStudentIdAndCursusId(studentId, cursusId))
        .thenReturn(true);

    assertThat(groupMembershipService.isStudentInCursus(studentId, cursusId)).isTrue();
  }

  @Test
  void should_return_false_when_student_is_not_in_cursus() {
    UUID studentId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    when(groupMembershipRepository.existsByStudentIdAndCursusId(studentId, cursusId))
        .thenReturn(false);

    assertThat(groupMembershipService.isStudentInCursus(studentId, cursusId)).isFalse();
  }

  @Test
  void should_find_active_cursus_ids_for_student() {
    UUID studentId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    when(groupMembershipRepository.findCursusIdsForStudent(studentId))
        .thenReturn(List.of(cursusId));

    assertThat(groupMembershipService.findActiveCursusIds(studentId)).containsExactly(cursusId);
  }

  @Test
  void should_return_empty_list_when_no_active_cursus_for_student() {
    UUID studentId = UUID.randomUUID();
    when(groupMembershipRepository.findCursusIdsForStudent(studentId)).thenReturn(List.of());

    assertThat(groupMembershipService.findActiveCursusIds(studentId)).isEmpty();
  }
}
