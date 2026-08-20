package hei.school.app.service;

import hei.school.app.dto.CourseValidationStatus;
import hei.school.app.dto.ExamScoreDetail;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
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

    var gradeIds = grades.stream().map(JGrade::getId).toList();
    var latestScores = scoreHistoryRepository.findLatestScoresByGradeIds(gradeIds);
    if (latestScores.isEmpty()) return BigDecimal.ZERO;

    double weightedSum = 0.0;
    double totalCoefficient = 0.0;

    for (JScoreHistory sh : latestScores) {
      JExam exam = sh.getGrade().getExam();
      double coeff = exam.getCoefficient().doubleValue();
      double score = sh.getScore().doubleValue();
      weightedSum += score * coeff;
      totalCoefficient += coeff;
    }

    if (totalCoefficient <= 0) return BigDecimal.ZERO;
    return BigDecimal.valueOf(weightedSum / totalCoefficient).setScale(2, RoundingMode.HALF_UP);
  }

  @Transactional(readOnly = true)
  public boolean isCourseValidated(UUID studentId, UUID courseId) {
    return calculateCourseAverage(studentId, courseId).compareTo(BigDecimal.valueOf(10.0)) >= 0;
  }

  @Transactional(readOnly = true)
  public boolean isGraduated(UUID studentId, UUID cursusId) {
    var courses = courseRepository.findByCursusId(cursusId);
    if (courses.isEmpty()) {
      log.warn("Cursus {} has no courses", cursusId);
      return false;
    }
    for (JCourse course : courses) {
      if (!isCourseValidated(studentId, course.getId())) {
        return false;
      }
    }
    return true;
  }

  @Transactional(readOnly = true)
  public List<CourseValidationStatus> getCourseValidationStatuses(UUID studentId, UUID cursusId) {
    var courses = courseRepository.findByCursusId(cursusId);
    return courses.stream()
        .map(
            c -> {
              var avg = calculateCourseAverage(studentId, c.getId());
              boolean valid = avg.compareTo(BigDecimal.valueOf(10.0)) >= 0;
              return new CourseValidationStatus(c.getId(), c.getTitle(), c.getCredit(), valid, avg);
            })
        .sorted(Comparator.comparing(CourseValidationStatus::title))
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ExamScoreDetail> getExamScoreDetails(UUID studentId, UUID courseId) {
    var grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    if (grades.isEmpty()) return List.of();

    var gradeIds = grades.stream().map(JGrade::getId).toList();
    var latestScores = scoreHistoryRepository.findLatestScoresByGradeIds(gradeIds);

    return latestScores.stream()
        .map(
            sh -> {
              JExam exam = sh.getGrade().getExam();
              return new ExamScoreDetail(
                  exam.getId(),
                  exam.getExamDate(),
                  exam.getCoefficient(),
                  sh.getScore(),
                  exam.getCoefficient().multiply(sh.getScore()).setScale(2, RoundingMode.HALF_UP));
            })
        .sorted(Comparator.comparing(ExamScoreDetail::examDate))
        .toList();
  }
}
