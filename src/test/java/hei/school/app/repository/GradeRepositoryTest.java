package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
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

  private JUser saveUser(String email, UserRole role) {
    return userRepository.save(
        JUser.builder().id(UUID.randomUUID()).firstName("A").lastName("B").role(role).email(email).password("x").build());
  }

  private JGrade saveGrade() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
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
            JExam.builder().examDate(Instant.parse("2026-06-15T09:00:00Z")).coefficient(new BigDecimal("2.5")).course(course).build());
    JUser student = saveUser("student+" + UUID.randomUUID() + "@hei.school", UserRole.STUDENT);

    return gradeRepository.save(JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());
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

    assertThat(gradeRepository.findByGradedBy_Id(grade.getGradedBy().getId())).containsExactly(grade);
  }

  @Test
  void should_return_true_when_grade_belongs_to_student() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.existsByIdAndStudentId(grade.getId(), grade.getStudent().getId())).isTrue();
  }

  @Test
  void should_return_false_when_grade_does_not_belong_to_student() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.existsByIdAndStudentId(grade.getId(), UUID.randomUUID())).isFalse();
  }

  @Test
  void should_find_course_id_by_grade_id() {
    JGrade grade = saveGrade();

    assertThat(gradeRepository.findCourseIdByGradeId(grade.getId())).contains(grade.getExam().getCourse().getId());
  }

  @Test
  void should_return_empty_when_grade_does_not_exist_for_course_id() {
    assertThat(gradeRepository.findCourseIdByGradeId(UUID.randomUUID())).isEmpty();
  }
}