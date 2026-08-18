package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ScoreHistoryRepositoryTest extends FacadeIT {

  @Autowired private ScoreHistoryRepository scoreHistoryRepository;
  @Autowired private GradeRepository gradeRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private CursusRepository cursusRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void should_find_history_ordered_by_graded_at() throws InterruptedException {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher =
        userRepository.save(
            JUser.builder().id(UUID.randomUUID()).firstName("A").lastName("B").role(UserRole.TEACHER).email("t@hei.school").password("x").build());
    JUser student =
        userRepository.save(
            JUser.builder().id(UUID.randomUUID()).firstName("C").lastName("D").role(UserRole.STUDENT).email("s@hei.school").password("x").build());
    JCourse course =
        courseRepository.save(JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());
    JExam exam =
        examRepository.save(
            JExam.builder().examDate(Instant.parse("2026-06-15T09:00:00Z")).coefficient(new BigDecimal("2.5")).course(course).build());
    JGrade grade = gradeRepository.save(JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());

    JScoreHistory first =
        scoreHistoryRepository.save(
            JScoreHistory.builder().grade(grade).score(new BigDecimal("8.00")).explanation("Première correction").build());
    Thread.sleep(10);
    JScoreHistory second =
        scoreHistoryRepository.save(
            JScoreHistory.builder().grade(grade).score(new BigDecimal("12.00")).explanation("Réclamation acceptée").build());

    assertThat(scoreHistoryRepository.findByGradeIdOrderByGradedAtAsc(grade.getId()))
        .containsExactly(first, second);
  }
}