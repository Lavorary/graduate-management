package hei.school.app.repository;

import hei.school.app.repository.model.JExam;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<JExam, UUID> {
  List<JExam> findByCourseId(UUID courseId);

  @Query("select e.course.id from JExam e where e.id = :examId")
  Optional<UUID> findCourseIdByExamId(UUID examId);

  @Query("select e.course.cursus.id from JExam e where e.id = :examId")
  Optional<UUID> findCursusIdByExamId(UUID examId);
}
