package hei.school.app.repository;

import hei.school.app.repository.model.JScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScoreHistoryRepository extends JpaRepository<JScoreHistory, UUID> {
    List<JScoreHistory> findByGradeIdOrderByGradedAtAsc(UUID gradeId);
}
