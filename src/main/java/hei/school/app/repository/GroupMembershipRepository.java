package hei.school.app.repository;

import hei.school.app.repository.model.JGroupMembership;
import hei.school.app.repository.model.JUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupMembershipRepository extends JpaRepository<JGroupMembership, UUID> {
  List<JGroupMembership> findByStudentIdOrderByStartDateAsc(UUID studentId);

  List<JGroupMembership> findByStudentIdAndEndDateIsNull(UUID studentId);

  List<JGroupMembership> findByGroupId(UUID groupId);

  @Query(
      """
      select case when count(gm) > 0 then true else false end
      from JGroupMembership gm
      join gm.group.cursus cur
      join JCourse co on co.cursus = cur
      join co.teachers t
      where gm.student.id = :studentId
        and t.id = :teacherId
        and (gm.endDate is null or gm.endDate > current_date)
      """)
  boolean existsTeacherAccessToStudent(UUID teacherId, UUID studentId);

  @Query(
      """
      select case when count(gm) > 0 then true else false end
      from JGroupMembership gm
      join gm.group.cursus cur
      where gm.student.id = :studentId
        and cur.id = :cursusId
        and (gm.endDate is null or gm.endDate > current_date)
      """)
  boolean existsByStudentIdAndCursusId(UUID studentId, UUID cursusId);

  @Query(
      """
      select distinct cur.id
      from JGroupMembership gm
      join gm.group.cursus cur
      where gm.student.id = :studentId
        and (gm.endDate is null or gm.endDate > current_date)
      """)
  List<UUID> findCursusIdsForStudent(UUID studentId);

  @Query(
      "SELECT gm.user FROM JGroupMembership gm WHERE gm.group.cursus.id = :cursusId AND gm.endDate"
          + " IS NULL")
  List<JUser> findActiveStudentsByCursusId(UUID cursusId);
}
