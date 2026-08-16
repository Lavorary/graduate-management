package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import hei.school.app.model.User;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class CourseMapperTest {

  @Autowired private CourseMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JCursus jCursus = JCursus.builder().id(UUID.randomUUID()).name("CS").build();
    JUser teacher1 = JUser.builder().id(UUID.randomUUID()).firstName("Prof A").build();
    JUser teacher2 = JUser.builder().id(UUID.randomUUID()).firstName("Prof B").build();

    JCourse jCourse =
        JCourse.builder()
            .id(UUID.randomUUID())
            .ref("CS101")
            .title("Algorithms")
            .credit(5)
            .cursus(jCursus)
            .teachers(Set.of(teacher1, teacher2))
            .build();

    Course domain = mapper.toModel(jCourse);

    assertThat(domain.title()).isEqualTo("Algorithms");
    assertThat(domain.cursus().name()).isEqualTo("CS");
    assertThat(domain.teachers()).hasSize(2);
    assertThat(domain.teachers())
        .extracting("firstName")
        .containsExactlyInAnyOrder("Prof A", "Prof B");
  }

  @Test
  void shouldMapDomainToJpa() {
    Cursus cursus = Cursus.builder().id(UUID.randomUUID()).name("Math").build();
    User teacher1 = User.builder().id(UUID.randomUUID()).firstName("Prof C").build();

    Course domain =
        Course.builder()
            .id(UUID.randomUUID())
            .ref("MATH101")
            .title("Calculus")
            .credit(4)
            .cursus(cursus)
            .teachers(Set.of(teacher1))
            .build();

    JCourse jCourse = mapper.toEntity(domain);

    assertThat(jCourse.getTitle()).isEqualTo("Calculus");
    assertThat(jCourse.getCursus().getName()).isEqualTo("Math");
    assertThat(jCourse.getTeachers()).hasSize(1);
    assertThat(jCourse.getTeachers().iterator().next().getFirstName()).isEqualTo("Prof C");
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JCourse> jList =
        List.of(
            JCourse.builder().id(UUID.randomUUID()).title("Course A").build(),
            JCourse.builder().id(UUID.randomUUID()).title("Course B").build());

    List<Course> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
    assertThat(domains.get(0).title()).isEqualTo("Course A");
    assertThat(domains.get(1).title()).isEqualTo("Course B");
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<Course> domains =
        List.of(
            Course.builder().id(UUID.randomUUID()).title("Course A").build(),
            Course.builder().id(UUID.randomUUID()).title("Course B").build());

    List<JCourse> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
    assertThat(jList.get(0).getTitle()).isEqualTo("Course A");
    assertThat(jList.get(1).getTitle()).isEqualTo("Course B");
  }
}
