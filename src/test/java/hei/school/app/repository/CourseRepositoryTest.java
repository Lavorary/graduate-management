package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CourseRepositoryTest extends FacadeIT {

  @Autowired private CourseRepository courseRepository;
  @Autowired private CursusRepository cursusRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void should_find_by_ref() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    courseRepository.save(
        JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());

    assertThat(courseRepository.findByRef("ALG101")).isPresent();
  }

  @Test
  void should_find_by_cursus_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    courseRepository.save(
        JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());

    assertThat(courseRepository.findByCursusId(cursus.getId())).hasSize(1);
  }

  @Test
  void should_find_by_teacher_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Marie")
                .lastName("T.")
                .role(UserRole.TEACHER)
                .email("marie@hei.school")
                .password("x")
                .build());
    courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    assertThat(courseRepository.findByTeachers_Id(teacher.getId())).hasSize(1);
  }

  @Test
  void should_return_true_when_course_exists_with_teacher() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Marie")
                .lastName("T.")
                .role(UserRole.TEACHER)
                .email("marie@hei.school")
                .password("x")
                .build());
    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    boolean result = courseRepository.existsByIdAndTeacherId(course.getId(), teacher.getId());

    assertThat(result).isTrue();
  }

  @Test
  void should_find_cursus_id_by_course_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .build());

    Optional<UUID> result = courseRepository.findCursusIdByCourseId(course.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(cursus.getId());
  }

  @Test
  void should_find_all_taught_by_teacher_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JUser teacher =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Marie")
                .lastName("T.")
                .role(UserRole.TEACHER)
                .email("marie@hei.school")
                .password("x")
                .build());
    courseRepository.save(
        JCourse.builder()
            .cursus(cursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(teacher))
            .build());

    List<JCourse> result = courseRepository.findAllTaughtByTeacherId(teacher.getId());

    assertThat(result).hasSize(1);
  }

  @Test
  void should_find_all_by_cursus_ids_in() {
    JCursus cursus1 =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCursus cursus2 =
        cursusRepository.save(
            JCursus.builder().name("MathLog").description("m").year("2026").build());

    courseRepository.save(
        JCourse.builder().cursus(cursus1).ref("ALG101").title("Algorithmique").credit(5).build());
    courseRepository.save(
        JCourse.builder().cursus(cursus2).ref("MATH101").title("Mathematics").credit(4).build());

    List<JCourse> result =
        courseRepository.findAllByCursusIdIn(List.of(cursus1.getId(), cursus2.getId()));

    assertThat(result).hasSize(2);
  }
}
