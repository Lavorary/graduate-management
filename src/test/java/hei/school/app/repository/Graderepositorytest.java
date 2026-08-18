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

  @Test
  void should_find_by_student_id_and_graded_by_and_exam_id() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher = saveUser("teacher@hei.school", UserRole.TEACHER);
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
    JUser student = saveUser("student@hei.school", UserRole.STUDENT);

    JGrade grade = gradeRepository.save(JGrade.builder().exam(exam).student(student).gradedBy(teacher).build());

    assertThat(gradeRepository.findByStudent_Id(student.getId())).containsExactly(grade);
    assertThat(gradeRepository.findByExam_Id(exam.getId())).containsExactly(grade);
    assertThat(gradeRepository.findByGradedBy_Id(teacher.getId())).containsExactly(grade);
  }
}
