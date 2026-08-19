package hei.school.app.repository;

import hei.school.app.repository.model.JCourse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<JCourse, UUID> {
  Optional<JCourse> findByRef(String ref);
  List<JCourse> findByCursusId(UUID cursusId);
  List<JCourse> findByTeachers_Id(UUID teacherId);
}
