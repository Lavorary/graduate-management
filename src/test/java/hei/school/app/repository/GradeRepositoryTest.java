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
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GradeRepositoryTest extends FacadeIT {

  @Autowired private GradeRepository gradeRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private CursusRepository cursusRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ScoreHistoryRepository scoreHistoryRepository;

  private JUser saveUser(String email, UserRole role) {
    return userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("A")
            .lastName("B")
            .role(role)
            .email(email)
            .password("x")
            .build());
  }

  private JGrade saveGrade() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher = saveUser("teacher+" + UUID.randomUUID() + "@hei.school", UserRole.TEACHER);
    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());
    JExam exam =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.parse("2026-06-15T09:00:00Z"))
                .coefficient(new BigDecimal("2.5"))
                .course(course)
                .build());
    JUser student = saveUser("student+" + UUID.randomUUID() + "@hei.school", UserRole.STUDENT);

    return gradeRepository.save(
        JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());
  }

  @Test
  void should_find_by_student_id() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.findByStudent_Id(grade.getStudent().getId())).containsExactly(grade);
  }

  @Test
  void should_find_by_exam_id() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.findByExam_Id(grade.getExam().getId())).containsExactly(grade);
  }

  @Test
  void should_find_by_graded_by_id() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.findByGradedBy_Id(grade.getGradedBy().getId()))
        .containsExactly(grade);
  }

  @Test
  void should_return_true_when_grade_belongs_to_student() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.existsByIdAndStudentId(grade.getId(), grade.getStudent().getId()))
        .isTrue();
  }

  @Test
  void should_return_false_when_grade_does_not_belong_to_student() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.existsByIdAndStudentId(grade.getId(), UUID.randomUUID())).isFalse();
  }

  @Test
  void should_find_course_id_by_grade_id() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.findCourseIdByGradeId(grade.getId()))
        .contains(grade.getExam().getCourse().getId());
  }

  @Test
  void should_return_empty_when_grade_does_not_exist_for_course_id() {
    assertThat(gradeRepository.findCourseIdByGradeId(UUID.randomUUID())).isEmpty();
  }

  @Test
  void should_calculate_average_score_for_student() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());

    JUser teacher = saveUser("teacher@hei.school", UserRole.TEACHER);
    JUser student = saveUser("student@hei.school", UserRole.STUDENT);

    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    JExam exam =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.now())
                .coefficient(BigDecimal.ONE)
                .course(course)
                .build());

    JGrade grade =
        gradeRepository.save(
            JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(12.5))
            .gradedAt(Instant.now().minusSeconds(3600))
            .explanation("First correction")
            .build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(15.5))
            .gradedAt(Instant.now())
            .explanation("Second correction")
            .build());

    Double average = gradeRepository.findAverageScoreByStudentId(student.getId());

    assertThat(average).isEqualTo(14.0);
  }

  @Test
  void should_calculate_average_score_for_student_with_multiple_grades() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());

    JUser teacher = saveUser("teacher@hei.school", UserRole.TEACHER);
    JUser student = saveUser("student@hei.school", UserRole.STUDENT);

    JCourse course1 =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    JExam exam1 =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.now())
                .coefficient(BigDecimal.ONE)
                .course(course1)
                .build());

    JGrade grade1 =
        gradeRepository.save(
            JGrade.builder().exam(exam1).student(student).gradedBy(teacher).build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade1)
            .score(BigDecimal.valueOf(14.0))
            .gradedAt(Instant.now())
            .explanation("Algorithmique grade")
            .build());

    JCourse course2 =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("BDD101")
                .title("Base de données")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    JExam exam2 =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.now())
                .coefficient(BigDecimal.ONE)
                .course(course2)
                .build());

    JGrade grade2 =
        gradeRepository.save(
            JGrade.builder().exam(exam2).student(student).gradedBy(teacher).build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade2)
            .score(BigDecimal.valueOf(16.0))
            .gradedAt(Instant.now())
            .explanation("BDD grade")
            .build());

    Double average = gradeRepository.findAverageScoreByStudentId(student.getId());

    assertThat(average).isEqualTo(15.0);
  }

  @Test
  void should_return_null_when_student_has_no_grades() {
    JUser student = saveUser("no_grade@hei.school", UserRole.STUDENT);

    Double average = gradeRepository.findAverageScoreByStudentId(student.getId());

    assertThat(average).isNull();
  }

  @Test
  void should_return_null_when_student_has_grades_but_no_score_history() {
    JGrade grade = saveGrade();

    Double average = gradeRepository.findAverageScoreByStudentId(grade.getStudent().getId());

    assertThat(average).isNull();
  }

  @Test
  void should_calculate_average_using_all_score_histories() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());

    JUser teacher = saveUser("teacher@hei.school", UserRole.TEACHER);
    JUser student = saveUser("student@hei.school", UserRole.STUDENT);

    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    JExam exam =
        examRepository.save(
            JExam.builder()
                .examDate(Instant.now())
                .coefficient(BigDecimal.ONE)
                .course(course)
                .build());

    JGrade grade =
        gradeRepository.save(
            JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(10.0))
            .gradedAt(Instant.now().minusSeconds(7200))
            .explanation("First attempt")
            .build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(12.0))
            .gradedAt(Instant.now().minusSeconds(3600))
            .explanation("Second attempt")
            .build());

    scoreHistoryRepository.save(
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(18.0))
            .gradedAt(Instant.now())
            .explanation("Final attempt")
            .build());

    Double average = gradeRepository.findAverageScoreByStudentId(student.getId());

    assertThat(average).isEqualTo(13.333333333333334);
  }
}
