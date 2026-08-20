package hei.school.app.repository;

import hei.school.app.repository.model.JGrade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GradeRepository extends JpaRepository<JGrade, UUID> {
  List<JGrade> findByStudent_Id(UUID studentId);

  List<JGrade> findByExam_Id(UUID examId);

  List<JGrade> findByGradedBy_Id(UUID teacherId);

  boolean existsByIdAndStudentId(UUID gradeId, UUID studentId);

  @Query("select g.exam.course.id from JGrade g where g.id = :gradeId")
  Optional<UUID> findCourseIdByGradeId(UUID gradeId);

  @Query("SELECT AVG(sh.score) FROM JScoreHistory sh " +
         "WHERE sh.grade.id IN (SELECT g.id FROM JGrade g WHERE g.student.id = :studentId)")
  Double findAverageScoreByStudentId(@Param("studentId") UUID studentId);
}
