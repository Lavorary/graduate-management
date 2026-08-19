package hei.school.app.service;

import hei.school.app.DTOs.ScoreHistoryDTO;
import hei.school.app.enums.Reason;
import hei.school.app.mapper.ScoreHistoryMapper;
import hei.school.app.model.ScoreHistory;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreHistoryService {

  private final ScoreHistoryRepository scoreHistoryRepository;
  private final GradeRepository gradeRepository;
  private final ScoreHistoryMapper scoreHistoryMapper;

  public ScoreHistoryDTO create(UUID gradeId, BigDecimal score, Reason reason, String explanation) {
    JGrade grade =
        gradeRepository
            .findById(gradeId)
            .orElseThrow(() -> new IllegalArgumentException("Grade with Id : " + gradeId + " not found"));

    JScoreHistory saved =
        scoreHistoryRepository.save(
            JScoreHistory.builder()
                .grade(grade)
                .score(score)
                .reason(reason)
                .explanation(explanation)
                .build());
    return toDto(saved);
  }

  public ScoreHistoryDTO getById(UUID id) {
    JScoreHistory entity =
        scoreHistoryRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("ScoreHistory with Id : " + id + " not found"));
    return toDto(entity);
  }
  
  public List<ScoreHistoryDTO> findByGrade(UUID gradeId) {
    return scoreHistoryRepository.findByGradeIdOrderByGradedAtAsc(gradeId).stream()
        .map(this::toDto)
        .toList();
  }

  private ScoreHistoryDTO toDto(JScoreHistory entity) {
    ScoreHistory model = scoreHistoryMapper.toModel(entity);
    return ScoreHistoryDTO.builder()
        .id(model.id())
        .gradeId(model.grade().id())
        .score(model.score())
        .gradedAt(model.gradedAt())
        .reason(model.reason())
        .explanation(model.explanation())
        .build();
  }
}