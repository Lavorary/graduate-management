package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
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
}
