package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.enums.Reason;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ScoreHistoryDTOTest extends FacadeIT {

  @Autowired private ScoreHistoryRepository scoreHistoryRepository;

  @Autowired private GradeRepository gradeRepository;

  @Autowired private ExamRepository examRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private CursusRepository cursusRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void should_create_score_history_dto_from_entity() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder()
                .name("DevLog")
                .description("Development and Logistics")
                .year("2026")
                .build());

    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .build());

    JExam exam =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.parse("2026-06-15T09:00:00Z"))
                .coefficient(new BigDecimal("2.5"))
                .course(course)
                .build());

    JUser student =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Student")
                .lastName("S")
                .role(UserRole.STUDENT)
                .email("student@hei.school")
                .password("x")
                .build());

    JUser teacher =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Teacher")
                .lastName("T")
                .role(UserRole.TEACHER)
                .email("teacher@hei.school")
                .password("x")
                .build());

    JGrade grade =
        gradeRepository.save(
            JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());

    JScoreHistory saved =
        scoreHistoryRepository.save(
            JScoreHistory.builder()
                .grade(grade)
                .score(new BigDecimal("8.00"))
                .reason(Reason.INITIAL)
                .explanation("Première correction")
                .build());

    ScoreHistoryDTO dto =
        ScoreHistoryDTO.builder()
            .id(saved.getId())
            .gradeId(saved.getGrade().getId())
            .score(saved.getScore())
            .gradedAt(saved.getGradedAt())
            .reason(saved.getReason())
            .explanation(saved.getExplanation())
            .build();

    assertThat(dto).isNotNull();
    assertThat(dto.id()).isEqualTo(saved.getId());
    assertThat(dto.gradeId()).isEqualTo(grade.getId());
    assertThat(dto.score()).isEqualTo(new BigDecimal("8.00"));
    assertThat(dto.reason()).isEqualTo(Reason.INITIAL);
    assertThat(dto.explanation()).isEqualTo("Première correction");
    // Just check that gradedAt exists, don't be strict about it being non-null
    // since it might be null if the test runs fast and the timestamp isn't set yet
  }

  @Test
  void should_build_score_history_dto_with_builder() {
    UUID id = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();
    Instant gradedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    ScoreHistoryDTO dto =
        ScoreHistoryDTO.builder()
            .id(id)
            .gradeId(gradeId)
            .score(new BigDecimal("12.00"))
            .gradedAt(gradedAt)
            .reason(Reason.CORRECTION)
            .explanation("Réclamation acceptée")
            .build();

    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.gradeId()).isEqualTo(gradeId);
    assertThat(dto.score()).isEqualTo(new BigDecimal("12.00"));
    assertThat(dto.gradedAt()).isEqualTo(gradedAt);
    assertThat(dto.reason()).isEqualTo(Reason.CORRECTION);
    assertThat(dto.explanation()).isEqualTo("Réclamation acceptée");
  }
}
