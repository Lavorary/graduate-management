package hei.school.app.repository;

import hei.school.app.repository.model.JExam;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<JExam, UUID> {
  List<JExam> findByCourseId(UUID courseId);
}
