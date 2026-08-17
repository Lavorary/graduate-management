package hei.school.app.repository;

import hei.school.app.repository.model.JExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<JExam, UUID> {
    List<JExam> findByCourseId(UUID courseId);
}
