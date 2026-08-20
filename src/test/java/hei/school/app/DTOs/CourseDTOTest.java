package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.UserRepository;
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
class CourseDTOTest extends FacadeIT {

  @Autowired private CourseRepository courseRepository;

  @Autowired private CursusRepository cursusRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void should_create_course_dto_from_entity() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
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

    JCourse saved =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .teachers(Set.of(teacher))
                .build());

    CourseDTO dto =
        CourseDTO.builder()
            .id(saved.getId())
            .cursusId(saved.getCursus().getId())
            .ref(saved.getRef())
            .title(saved.getTitle())
            .credit(saved.getCredit())
            .teacherIds(Set.of(teacher.getId()))
            .build();

    assertThat(dto).isNotNull();
    assertThat(dto.id()).isEqualTo(saved.getId());
    assertThat(dto.cursusId()).isEqualTo(cursus.getId());
    assertThat(dto.ref()).isEqualTo("ALG101");
    assertThat(dto.title()).isEqualTo("Algorithmique");
    assertThat(dto.credit()).isEqualTo(5);
    assertThat(dto.teacherIds()).hasSize(1);
    assertThat(dto.teacherIds()).contains(teacher.getId());
  }

  @Test
  void should_build_course_dto_with_builder() {
    UUID id = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();

    CourseDTO dto =
        CourseDTO.builder()
            .id(id)
            .cursusId(cursusId)
            .ref("DB101")
            .title("Databases")
            .credit(4)
            .teacherIds(Set.of(teacherId))
            .build();

    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.cursusId()).isEqualTo(cursusId);
    assertThat(dto.ref()).isEqualTo("DB101");
    assertThat(dto.title()).isEqualTo("Databases");
    assertThat(dto.credit()).isEqualTo(4);
    assertThat(dto.teacherIds()).hasSize(1);
  }

  @Test
  void should_create_course_dto_with_multiple_teachers() {
    UUID id = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    UUID teacherId1 = UUID.randomUUID();
    UUID teacherId2 = UUID.randomUUID();

    CourseDTO dto =
        CourseDTO.builder()
            .id(id)
            .cursusId(cursusId)
            .ref("MATH101")
            .title("Mathematics")
            .credit(6)
            .teacherIds(Set.of(teacherId1, teacherId2))
            .build();

    assertThat(dto.teacherIds()).hasSize(2);
    assertThat(dto.teacherIds()).contains(teacherId1, teacherId2);
  }
}
