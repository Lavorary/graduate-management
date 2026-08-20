package hei.school.app.repository;

import hei.school.app.repository.model.JScoreHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScoreHistoryRepository extends JpaRepository<JScoreHistory, UUID> {
  List<JScoreHistory> findByGradeIdOrderByGradedAtAsc(UUID gradeId);

  @Query(
      """
SELECT sh FROM JScoreHistory sh
WHERE sh.grade.id IN :gradeIds
  AND sh.gradedAt = (SELECT MAX(sh2.gradedAt) FROM JScoreHistory sh2 WHERE sh2.grade.id = sh.grade.id)
""")
  List<JScoreHistory> findLatestScoresByGradeIds(List<UUID> gradeIds);
}
