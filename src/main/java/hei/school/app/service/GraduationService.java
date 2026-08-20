package hei.school.app.service;

import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraduationService {
  private final CourseRepository courseRepository;
  private final GradeRepository gradeRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;

  @Transactional(readOnly = true)
  public BigDecimal calculateCourseAverage(UUID studentId, UUID courseId) {
    var grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    if (grades.isEmpty()) return BigDecimal.ZERO;

    var latestScores =
        scoreHistoryRepository.findLatestScoresByGradeIds(
            grades.stream().map(JGrade::getId).toList());
    if (latestScores.isEmpty()) return BigDecimal.ZERO;

    double weightedSum = 0.0;
    double totalCoeff = 0.0;
    for (JScoreHistory sh : latestScores) {
      double coeff = sh.getGrade().getExam().getCoefficient().doubleValue();
      weightedSum += sh.getScore().doubleValue() * coeff;
      totalCoeff += coeff;
    }
    return totalCoeff > 0
        ? BigDecimal.valueOf(weightedSum / totalCoeff).setScale(2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;
  }

  @Transactional(readOnly = true)
  public boolean isGraduated(UUID studentId, UUID cursusId) {
    return courseRepository.findByCursusId(cursusId).stream()
        .allMatch(c -> calculateCourseAverage(studentId, c.getId()).compareTo(BigDecimal.TEN) >= 0);
  }
}
