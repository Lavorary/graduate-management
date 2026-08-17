package hei.school.app.repository;

import hei.school.app.repository.model.JGrade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<JGrade, UUID> {
  List<JGrade> findByStudent_Id(UUID studentId);

  List<JGrade> findByExam_Id(UUID examId);

  List<JGrade> findByGradedBy_Id(UUID teacherId);
}
