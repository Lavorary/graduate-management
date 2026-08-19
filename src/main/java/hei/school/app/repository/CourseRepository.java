package hei.school.app.repository;

import hei.school.app.repository.model.JCourse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<JCourse, UUID> {
  Optional<JCourse> findByRef(String ref);

  List<JCourse> findByCursusId(UUID cursusId);

  List<JCourse> findByTeachers_Id(UUID teacherId);

  @Query(
      """
      select cas when count(c) > 0 then true else false end
      from JCourse c join c.teachers t
      where c.id = :courseId and t.id = :teacherId
      """)
  boolean existsByIdAndTeacherId(UUID courseId, UUID teacherId);

  @Query(
      """
      select c.cursus.id from JCourse c where c.id = :courseId
      """)
  Optional<UUID> findCursusIdByCourseId(UUID courseId);

  @Query("select c from JCourse c join c.teachers t where t.id = :teacherId")
  List<JCourse> findAllTaughtByTeacherId(UUID teacherId);

  List<JCourse> findAllByCursusIdIn(List<UUID> cursusIds);
}
