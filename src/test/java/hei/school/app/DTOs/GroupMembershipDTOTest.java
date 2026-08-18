package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.GroupMembershipRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.UserRepository;
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
class GroupMembershipDTOTest extends FacadeIT {

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void should_create_group_membership_dto_from_entity() {
        JUser student = userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Student")
                .lastName("S")
                .role(UserRole.STUDENT)
                .email("student@hei.school")
                .password("x")
                .build()
        );

        JGroup group = groupRepository.save(
            JGroup.builder()
                .ref("G1")
                .build()
        );

        LocalDate startDate = LocalDate.now();

        JGroupMembership saved = groupMembershipRepository.save(
            JGroupMembership.builder()
                .student(student)
                .group(group)
                .startDate(startDate)
                .endDate(null)
                .build()
        );

        GroupMembershipDTO dto = GroupMembershipDTO.builder()
            .id(saved.getId())
            .startDate(saved.getStartDate())
            .endDate(saved.getEndDate())
            .studentId(saved.getStudent().getId())
            .groupId(saved.getGroup().getId())
            .build();

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.startDate()).isEqualTo(startDate);
        assertThat(dto.endDate()).isNull();
        assertThat(dto.studentId()).isEqualTo(student.getId());
        assertThat(dto.groupId()).isEqualTo(group.getId());
    }

    @Test
    void should_build_group_membership_dto_with_builder() {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(6);

        GroupMembershipDTO dto = GroupMembershipDTO.builder()
            .id(id)
            .startDate(startDate)
            .endDate(endDate)
            .studentId(studentId)
            .groupId(groupId)
            .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.startDate()).isEqualTo(startDate);
        assertThat(dto.endDate()).isEqualTo(endDate);
        assertThat(dto.studentId()).isEqualTo(studentId);
        assertThat(dto.groupId()).isEqualTo(groupId);
    }
}