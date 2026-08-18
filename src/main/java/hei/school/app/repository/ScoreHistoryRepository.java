package hei.school.app.repository;

import hei.school.app.repository.model.JScoreHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreHistoryRepository extends JpaRepository<JScoreHistory, UUID> {
  List<JScoreHistory> findByGradeIdOrderByGradedAtAsc(UUID gradeId);
}
