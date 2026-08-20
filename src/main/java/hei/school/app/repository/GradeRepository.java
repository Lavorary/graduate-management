package hei.school.app.repository;

import hei.school.app.repository.model.JGrade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GradeRepository extends JpaRepository<JGrade, UUID> {
  List<JGrade> findByStudent_Id(UUID studentId);

  List<JGrade> findByExam_Id(UUID examId);

  List<JGrade> findByGradedBy_Id(UUID teacherId);

  boolean existsByIdAndStudentId(UUID gradeId, UUID studentId);

  @Query("select g.exam.course.id from JGrade g where g.id = :gradeId")
  Optional<UUID> findCourseIdByGradeId(UUID gradeId);

  @Query("select g from JGrade g where g.student.id = :studentId and g.exam.course.id = :coourseId")
  List<JGrade> findByStudentIdAndCourseId(UUID sutdentId, UUID courseId);
}
