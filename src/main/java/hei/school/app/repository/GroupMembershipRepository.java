package hei.school.app.repository;

import hei.school.app.repository.model.JGroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupMembershipRepository extends JpaRepository<JGroupMembership, UUID> {
    List<JGroupMembership> findByStudentIdOrderByStartDateAsc(UUID studentId);
    List<JGroupMembership> findByStudentIdAndEndDateIsNull(UUID studentId);
    List<JGroupMembership> findByGroupId(UUID groupId);
}
