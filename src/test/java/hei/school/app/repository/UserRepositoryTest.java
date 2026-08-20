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
class UserRepositoryTest extends FacadeIT {

  @Autowired private UserRepository userRepository;
  @Autowired private CursusRepository cursusRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private GradeRepository gradeRepository;

  @Test
  void should_find_by_email() {
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jean")
            .lastName("R.")
            .role(UserRole.STUDENT)
            .email("jean@hei.school")
            .password("x")
            .build());

    assertThat(userRepository.findByEmail("jean@hei.school")).isPresent();
    assertThat(userRepository.findByEmail("inconnu@hei.school")).isEmpty();
  }

  @Test
  void should_find_by_role() {
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Marie")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("marie@hei.school")
            .password("x")
            .build());
    userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jean")
            .lastName("R.")
            .role(UserRole.STUDENT)
            .email("jean@hei.school")
            .password("x")
            .build());

    assertThat(userRepository.findByRole(UserRole.TEACHER))
        .extracting(JUser::getEmail)
        .containsExactly("marie@hei.school");
  }



  @Test
  void should_find_graduates_by_cursus_id() {
    // Given
    JCursus cursus = cursusRepository.save(
        JCursus.builder()
            .name("DevLog")
            .description("Development Logistics")
            .year("2026")
            .build());

    JUser teacher = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Teacher")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("teacher@hei.school")
            .password("x")
            .build());

    JUser student1 = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .email("john@hei.school")
            .password("x")
            .build());

    JUser student2 = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Jane")
            .lastName("Smith")
            .role(UserRole.STUDENT)
            .email("jane@hei.school")
            .password("x")
            .build());


    JUser otherStudent = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Bob")
            .lastName("Johnson")
            .role(UserRole.STUDENT)
            .email("bob@hei.school")
            .password("x")
            .build());

    JCourse course = courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    JExam exam = examRepository.save(
        JExam.builder()
            .examDate(Instant.now())
            .coefficient(BigDecimal.ONE)
            .course(course)
            .build());

    gradeRepository.save(
        JGrade.builder()
            .exam(exam)
            .student(student1)
            .gradedBy(teacher)
            .build());

    gradeRepository.save(
        JGrade.builder()
            .exam(exam)
            .student(student2)
            .gradedBy(teacher)
            .build());


    var graduates = userRepository.findGraduatesByCursusId(cursus.getId());


    assertThat(graduates)
        .extracting(JUser::getEmail)
        .containsExactlyInAnyOrder("john@hei.school", "jane@hei.school")
        .doesNotContain("bob@hei.school");
  }

  @Test
  void should_return_empty_list_when_no_graduates_for_cursus() {

    JCursus cursus = cursusRepository.save(
        JCursus.builder()
            .name("EmptyCursus")
            .description("No students")
            .year("2026")
            .build());


    var graduates = userRepository.findGraduatesByCursusId(cursus.getId());


    assertThat(graduates).isEmpty();
  }

  @Test
  void should_return_empty_list_when_cursus_has_no_grades() {

    JCursus cursus = cursusRepository.save(
        JCursus.builder()
            .name("NoGrades")
            .description("No grades yet")
            .year("2026")
            .build());

    JUser student = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Student")
            .lastName("S.")
            .role(UserRole.STUDENT)
            .email("student@hei.school")
            .password("x")
            .build());


    var graduates = userRepository.findGraduatesByCursusId(cursus.getId());


    assertThat(graduates).isEmpty();
  }

  @Test
  void should_find_graduates_without_duplicates_when_multiple_courses() {
    // Given
    JCursus cursus = cursusRepository.save(
        JCursus.builder()
            .name("DevLog")
            .description("Development Logistics")
            .year("2026")
            .build());

    JUser teacher = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Teacher")
            .lastName("T.")
            .role(UserRole.TEACHER)
            .email("teacher@hei.school")
            .password("x")
            .build());

    JUser student = userRepository.save(
        JUser.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .email("john@hei.school")
            .password("x")
            .build());


    JCourse course1 = courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    JCourse course2 = courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("BDD101")
            .title("Base de données")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    JExam exam1 = examRepository.save(
        JExam.builder()
            .examDate(Instant.now())
            .coefficient(BigDecimal.ONE)
            .course(course1)
            .build());

    JExam exam2 = examRepository.save(
        JExam.builder()
            .examDate(Instant.now())
            .coefficient(BigDecimal.ONE)
            .course(course2)
            .build());


    gradeRepository.save(
        JGrade.builder()
            .exam(exam1)
            .student(student)
            .gradedBy(teacher)
            .build());

    gradeRepository.save(
        JGrade.builder()
            .exam(exam2)
            .student(student)
            .gradedBy(teacher)
            .build());


    var graduates = userRepository.findGraduatesByCursusId(cursus.getId());


    assertThat(graduates)
        .hasSize(1)
        .extracting(JUser::getEmail)
        .containsExactly("john@hei.school");
  }
}