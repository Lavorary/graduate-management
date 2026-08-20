package hei.school.app.service;

import hei.school.app.DTOs.GroupMembershipDTO;
import hei.school.app.mapper.GroupMembershipMapper;
import hei.school.app.model.GroupMembership;
import hei.school.app.repository.GroupMembershipRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JGroup;
import hei.school.app.repository.model.JGroupMembership;
import hei.school.app.repository.model.JUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMembershipService {

  private final GroupMembershipRepository groupMembershipRepository;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final GroupMembershipMapper groupMembershipMapper;

  public GroupMembershipDTO create(
      LocalDate startDate, LocalDate endDate, UUID studentId, UUID groupId) {
    JUser student =
        userRepository
            .findById(studentId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Student with Id : " + studentId + " not found"));
    JGroup group =
        groupRepository
            .findById(groupId)
            .orElseThrow(
                () -> new IllegalArgumentException("Group with Id : " + groupId + " not found"));

    JGroupMembership saved =
        groupMembershipRepository.save(
            JGroupMembership.builder()
                .startDate(startDate)
                .endDate(endDate)
                .student(student)
                .group(group)
                .build());
    return toDto(saved);
  }

  public GroupMembershipDTO getById(UUID id) {
    JGroupMembership entity =
        groupMembershipRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("GroupMembership with Id : " + id + " not found"));
    return toDto(entity);
  }

  public List<GroupMembershipDTO> findByStudent(UUID studentId) {
    return groupMembershipRepository.findByStudentIdOrderByStartDateAsc(studentId).stream()
        .map(this::toDto)
        .toList();
  }

  public Optional<GroupMembership> findActiveByStudent(UUID studentId) {
    return groupMembershipRepository.findByStudentIdAndEndDateIsNull(studentId).stream()
        .findFirst()
        .map(groupMembershipMapper::toModel);
  }

  public List<GroupMembershipDTO> findByGroup(UUID groupId) {
    return groupMembershipRepository.findByGroupId(groupId).stream().map(this::toDto).toList();
  }

  public boolean hasTeacherAccessToStudent(UUID teacherId, UUID studentId) {
    return groupMembershipRepository.existsTeacherAccessToStudent(teacherId, studentId);
  }

  public boolean isStudentInCursus(UUID studentId, UUID cursusId) {
    return groupMembershipRepository.existsByStudentIdAndCursusId(studentId, cursusId);
  }

  public List<UUID> findActiveCursusIds(UUID studentId) {
    return groupMembershipRepository.findCursusIdsForStudent(studentId);
  }

  private GroupMembershipDTO toDto(JGroupMembership entity) {
    GroupMembership model = groupMembershipMapper.toModel(entity);
    return GroupMembershipDTO.builder()
        .id(model.id())
        .startDate(model.startDate())
        .endDate(model.endDate())
        .studentId(model.student().id())
        .groupId(model.group().id())
        .build();
  }

  @Transactional(readOnly = true)
  public List<JUser> getActiveStudentsByCursusId(UUID cursusId) {
    return groupMembershipRepository.findActiveStudentsByCursusId(cursusId);
  }
}
