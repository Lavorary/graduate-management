package hei.school.app.service;

import hei.school.app.enums.Reason;
import hei.school.app.mapper.GradeMapper;
import hei.school.app.model.Grade;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GradeService {
  private final ExamRepository examRepository;
  private final UserRepository userRepository;
  private final GradeRepository gradeRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;
  private final GradeMapper gradeMapper;

  @Transactional
  public Grade createGrade(
      UUID examId, UUID studentId, UUID gradedById, BigDecimal score, String explanation) {
    // 1. Save JGrade
    JGrade grade =
        JGrade.builder()
            .exam(examRepository.findById(examId).orElseThrow())
            .student(userRepository.findById(studentId).orElseThrow())
            .gradedBy(userRepository.findById(gradedById).orElseThrow())
            .build();
    gradeRepository.save(grade);

    // 2. Save initial ScoreHistory
    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .grade(grade)
            .score(score)
            .reason(Reason.INITIAL)
            .explanation(explanation)
            .build());
    return gradeMapper.toModel(grade);
  }
}
